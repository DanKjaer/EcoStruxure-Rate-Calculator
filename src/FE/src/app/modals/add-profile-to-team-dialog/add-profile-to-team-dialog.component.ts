import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Profile, Team, TeamDTO, TeamProfile, TeamProfileDTO} from '../../models';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
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
import {NgIf} from '@angular/common';

@Component({
    selector: 'app-add-profile-to-team-dialog',
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
        FormsModule
    ],
    templateUrl: './add-profile-to-team-dialog.component.html',
    styleUrl: './add-profile-to-team-dialog.component.css'
})
export class AddProfileToTeamDialogComponent implements OnInit{
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
    private formBuilder: FormBuilder
  ) {}

   async ngOnInit(): Promise<void> {
    this.teamProfileForm = this.formBuilder.group({
      costAllocationPercentage: [0, Validators.required],
      hourAllocationPercentage: [0, Validators.required]
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

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedTeams = $event.source.selectedOptions.selected.map(team =>
      team.value);
  }

  async onSave() {
    let newTeamProfiles: TeamProfile[] = [];
    this.selectedTeams.forEach(teamProfile => {
      teamProfile.allocatedCost = this.calculateCostAllocation(teamProfile);
      teamProfile.allocatedHours = this.calculateHourAllocation(teamProfile);
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

  private calculateCostAllocation(teamProfile: TeamProfile): number {
    if (teamProfile.allocationPercentageCost) {
      return teamProfile.profile!.annualCost! * (teamProfile.allocationPercentageCost / 100);
    }
    return 0;
  }

  private calculateHourAllocation(teamProfile: TeamProfile): number {
    if (teamProfile.allocationPercentageHours) {
      return teamProfile.profile!.annualHours! * (teamProfile.allocationPercentageHours / 100);
    }
    return 0;
  }
}
