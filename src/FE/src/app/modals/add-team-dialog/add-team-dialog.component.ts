import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {MatDialogModule} from '@angular/material/dialog';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {
  FormArray,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {MatFormField, MatLabel, MatPrefix, MatSuffix} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatListModule, MatSelectionListChange} from '@angular/material/list';
import {TeamsService} from '../../services/teams.service';
import {ProfileService} from '../../services/profile.service';
import {Profile, Team, TeamProfile} from '../../models';
import {SnackbarService} from '../../services/snackbar.service';
import {NgForOf, NgIf} from '@angular/common';
import {CalculationsService} from '../../services/calculations.service';
import {of} from 'rxjs';

@Component({
  selector: 'app-add-team-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    TranslateModule,
    FormsModule,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    MatButton,
    MatIcon,
    MatListModule,
    MatSuffix,
    NgIf,
    NgForOf
  ],
  templateUrl: './add-team-dialog.component.html',
  styleUrl: './add-team-dialog.component.css'
})
export class AddTeamDialogComponent implements OnInit {
  teamForm!: FormGroup;
  profileList: Profile[] = [];
  selectedProfiles: TeamProfile[] = [];
  @Output() teamAdded = new EventEmitter<Team>();

  constructor(
    private formBuilder: FormBuilder,
    private teamService: TeamsService,
    private profileService: ProfileService,
    private snackBar: SnackbarService,
    private calculationsService: CalculationsService,
    private translate: TranslateService) {
  }

  async ngOnInit() {
    this.teamForm = this.formBuilder.group({
      name: ['', Validators.required],
      allocations: this.formBuilder.array([]) // Dynamic FormArray for selected profiles
    });
    this.profileList = await this.profileService.getProfiles();
    this.profileList.sort((a, b) => {
      return a.name.localeCompare(b.name);
    });
  }

  async onSave() {
    this.selectedProfiles.forEach((teamProfile, index) => {
      const allocationForm = this.allocations.at(index) as FormGroup;
      teamProfile.allocationPercentageCost = allocationForm.get('allocationPercentageCost')!.value;
      teamProfile.allocationPercentageHours = allocationForm.get('allocationPercentageHours')!.value;
      teamProfile.allocatedCost = this.calculationsService.calculateCostAllocation(teamProfile);
      teamProfile.allocatedHours = this.calculationsService.calculateHourAllocation(teamProfile);
    });

    const team: Team = {
      name: this.teamForm.value.name,
      teamProfiles: this.selectedProfiles,
      markupPercentage: 0,
      grossMarginPercentage: 0
    };

    try {
      const newTeam = await this.teamService.postTeam(team);

      if (newTeam.teamId != undefined) {
        this.teamAdded.emit(newTeam);
        this.snackBar.openSnackBar(this.translate.instant('SUCCESS_TEAM_CREATED'), true);
      } else {
        this.snackBar.openSnackBar(this.translate.instant('ERROR_TEAM_CREATED'), false);
      }
    } catch (error) {
      this.snackBar.openSnackBar(this.translate.instant('ERROR_TEAM_CREATED'), false);
    }
  }

  isSaveDisabled(): boolean {
    return this.teamForm.invalid || this.allocations.invalid;
  }

  get allocations(): FormArray {
    return this.teamForm.get('allocations') as FormArray;
  }

  getFormGroupAt(index: number): FormGroup {
    return this.allocations.at(index) as FormGroup;
  }

  onSelectionChange(event: any): void {
    const selectedProfiles = event.source.selectedOptions.selected.map((option: any) => option.value);

    // Update selected profiles and reset FormArray
    this.selectedProfiles = selectedProfiles.map((profile: Profile) => ({
      profile,
      allocationPercentageCost: 0,
      allocationPercentageHours: 0
    }));

    this.allocations.clear();
    this.selectedProfiles.forEach(profile => {
      this.allocations.push(
        this.formBuilder.group({
          allocationPercentageCost: [
            null,
            [Validators.required, Validators.min(0), Validators.max(100)]
          ],
          allocationPercentageHours: [
            null,
            [Validators.required, Validators.min(0), Validators.max(100)]
          ]
        })
      );
    });
  }
}
