import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Profile, Team, TeamDTO, TeamProfile, TeamProfileDTO} from '../../models';
import {FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {TeamsService} from '../../services/teams.service';
import {SnackbarService} from '../../services/snackbar.service';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {MatList, MatListItem, MatListOption, MatSelectionList, MatSelectionListChange} from '@angular/material/list';
import {MatButton} from '@angular/material/button';
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from '@angular/material/dialog';
import {MatDivider} from '@angular/material/divider';
import {MatFormField, MatLabel, MatSuffix} from '@angular/material/form-field';
import {MatIcon} from '@angular/material/icon';
import {MatInput} from '@angular/material/input';
import {NgForOf, NgIf} from '@angular/common';
import {CalculationsService} from '../../services/calculations.service';

@Component({
  selector: 'app-add-profile-to-team-dialog',
  standalone: true,
  imports: [
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
    NgIf,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    NgForOf
  ],
  templateUrl: './add-profile-to-team-dialog.component.html',
  styleUrl: './add-profile-to-team-dialog.component.css'
})
export class AddProfileToTeamDialogComponent implements OnInit {
  @Output() addedProfileToTeam = new EventEmitter<TeamProfile[]>();
  @Input() profile!: Profile;
  @Input() teamProfiles!: TeamProfile[];
  teamList: TeamProfile[] = [];
  selectedTeams: TeamProfile[] = [];
  teamProfileForm!: FormGroup;


  constructor(
    private teamService: TeamsService,
    private snackBar: SnackbarService,
    private translate: TranslateService,
    private calculationsService: CalculationsService,
    private formBuilder: FormBuilder
  ) {
  }

  async ngOnInit(): Promise<void> {
    this.teamProfileForm = this.formBuilder.group({
      allocations: this.formBuilder.array([])
    });
    let teams = await this.teamService.getTeams();
    let teamsAvailable = teams.filter(potentialTeam => {
      return !this.teamProfiles.some(teamProfile =>
        teamProfile.team!.teamId === potentialTeam.teamId);
    });
    teamsAvailable.forEach(team => {
      let newTeamProfile: TeamProfile = {
        team: team,
        profile: this.profile,
      };
      this.teamList.push(newTeamProfile);
    });
  }

  isSaveDisabled(): boolean {
    return this.teamProfileForm.invalid || this.selectedTeams.length === 0;
  }

  get allocations(): FormArray {
    return this.teamProfileForm.get('allocations') as FormArray;
  }

  getFormGroupAt(index: number): FormGroup {
    return this.allocations.at(index) as FormGroup;
  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedTeams = $event.source.selectedOptions.selected.map(team =>
      team.value);

    this.allocations.clear();

    this.selectedTeams.forEach(team => {
      let allocationFormGroup = this.formBuilder.group({
        allocationPercentageCost: [null, [Validators.required, Validators.min(0), Validators.max(100)]],
        allocationPercentageHours: [null, [Validators.required, Validators.min(0), Validators.max(100)]]
      });
      this.allocations.push(allocationFormGroup);
    });
  }

  async onSave() {
    let newTeamProfiles: TeamProfile[] = [];
    this.selectedTeams.forEach((teamProfile, index) => {
      const allocationForm = this.allocations.at(index) as FormGroup;
      teamProfile.allocationPercentageCost = allocationForm.get('allocationPercentageCost')!.value;
      teamProfile.allocationPercentageHours = allocationForm.get('allocationPercentageHours')!.value;
      teamProfile.allocatedCost = this.calculationsService.calculateCostAllocation(teamProfile);
      teamProfile.allocatedHours = this.calculationsService.calculateHourAllocation(teamProfile);
      newTeamProfiles.push(teamProfile);
    });
    let response = await this.teamService.addProfileToTeams(newTeamProfiles)
    if (response) {
      this.snackBar.openSnackBar(this.translate.instant('team.addProfileToTeamSuccess'), true);
      this.addedProfileToTeam.emit(response);
    } else {
      this.snackBar.openSnackBar(this.translate.instant('team.addProfileToTeamError'), false);
    }
  }
}
