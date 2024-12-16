import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from '@angular/material/dialog';
import {MatDivider} from '@angular/material/divider';
import {MatFormField, MatLabel, MatSuffix} from '@angular/material/form-field';
import {MatIcon} from '@angular/material/icon';
import {MatInput} from '@angular/material/input';
import {MatList, MatListItem, MatListOption, MatSelectionList, MatSelectionListChange} from '@angular/material/list';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {Profile, Team, TeamDTO, TeamProfile, TeamProfileDTO} from '../../models';
import {ProfileService} from '../../services/profile.service';
import {SnackbarService} from '../../services/snackbar.service';
import {TeamsService} from '../../services/teams.service';
import {NgForOf, NgIf} from "@angular/common";
import {CalculationsService} from '../../services/calculations.service';
import {GenerateDTOService} from '../../services/generate-dto.service';

@Component({
  selector: 'app-add-to-team-dialog',
  standalone: true,
  imports: [
    FormsModule,
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    MatDivider,
    MatFormField,
    MatIcon,
    MatInput,
    MatLabel,
    MatList,
    MatListItem,
    MatListOption,
    MatSelectionList,
    MatSuffix,
    ReactiveFormsModule,
    TranslateModule,
    NgIf,
    NgForOf,
  ],
  templateUrl: './add-to-team-dialog.component.html',
  styleUrl: './add-to-team-dialog.component.css'
})
export class AddToTeamDialogComponent implements OnInit {
  profileForm!: FormGroup;
  teamProfileList: TeamProfile[] = [];
  selectedTeamProfiles: TeamProfile[] = [];
  @Output() AddToTeam = new EventEmitter<Team>();
  @Input() team!: Team;


  constructor(
    private profileService: ProfileService,
    private teamService: TeamsService,
    private snackbarService: SnackbarService,
    private calculationsService: CalculationsService,
    private translateService: TranslateService,
    private generateDTOService: GenerateDTOService,
    private formbuilder: FormBuilder
  ) {
  }

  async ngOnInit(): Promise<void> {
    this.profileForm = this.formbuilder.group({
      allocations: this.formbuilder.array([])
    });
    let profiles = await this.profileService.getProfiles();
    let potentialTeamProfiles = profiles.map(profile => {
      let teamProfile: TeamProfile = {
        team: {...this.team, teamProfiles: undefined},
        profile: profile!,
      };
      return teamProfile;
    });
    this.teamProfileList = potentialTeamProfiles.filter(potentialTeamProfile =>
      !this.team.teamProfiles!.some(alreadyProfile =>
        alreadyProfile.profile!.profileId === potentialTeamProfile.profile!.profileId));
  }

  isSaveDisabled(): boolean {
    return this.profileForm.invalid || this.selectedTeamProfiles.length === 0;
  }

  get allocations(): FormArray {
    return this.profileForm.get('allocations') as FormArray;
  }

  getFormGroupAt(index: number): FormGroup {
    return this.allocations.at(index) as FormGroup;
  }

  async onSave() {
    this.selectedTeamProfiles.forEach((teamProfile, index) => {
      const allocationForm = this.allocations.at(index) as FormGroup;
      teamProfile.allocationPercentageCost = allocationForm.get('allocationPercentageCost')!.value;
      teamProfile.allocationPercentageHours = allocationForm.get('allocationPercentageHours')!.value;
      teamProfile.allocatedCost = this.calculationsService.calculateCostAllocation(teamProfile);
      teamProfile.allocatedHours = this.calculationsService.calculateHourAllocation(teamProfile);
    });
    this.team.teamProfiles!.push(...this.selectedTeamProfiles);
    let DTO: TeamDTO = this.generateDTOService.createTeamDTO(this.team);
    try {
      const updatedTeam = await this.teamService.putTeam(DTO);

      if (updatedTeam != undefined) {
        this.AddToTeam.emit(updatedTeam);
        this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_TEAM_UPDATED'), true);
      } else {
        this.snackbarService.openSnackBar(this.translateService.instant('ERROR_TEAM_UPDATED'), false);
      }
    } catch (error: any) {
      console.error(error);
      this.snackbarService.openSnackBar(this.translateService.instant('ERROR_TEAM_UPDATED'), false);
    }
  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedTeamProfiles = $event.source.selectedOptions.selected.map(teamProfile =>
      teamProfile.value);

    this.allocations.clear();
    this.selectedTeamProfiles.forEach(profile => {
      this.allocations.push(
        this.formbuilder.group({
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
