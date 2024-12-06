import {Component, EventEmitter, Inject, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from '@angular/material/dialog';
import {MatDivider} from '@angular/material/divider';
import {MatFormField, MatLabel, MatSuffix} from '@angular/material/form-field';
import {MatIcon} from '@angular/material/icon';
import {MatInput} from '@angular/material/input';
import {MatList, MatListItem, MatListOption, MatSelectionList, MatSelectionListChange} from '@angular/material/list';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {Team, TeamDTO, TeamProfile, TeamProfileDTO} from '../../models';
import {ProfileService} from '../../services/profile.service';
import {SnackbarService} from '../../services/snackbar.service';
import {TeamsService} from '../../services/teams.service';
import {NgForOf, NgIf} from "@angular/common";

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
  ],
  templateUrl: './add-to-team-dialog.component.html',
  styleUrl: './add-to-team-dialog.component.css'
})
export class AddToTeamDialogComponent implements OnInit {
  profileForm!: FormGroup;
  teamProfileList:  TeamProfile[] = [];
  selectedTeamProfiles: TeamProfile[] = [];
  @Output() AddToTeam = new EventEmitter<Team>();
  @Input() team!: Team;


  constructor(
    private formBuilder: FormBuilder,
    private profileService: ProfileService,
    private teamService: TeamsService,
    private snackBar: SnackbarService,
    private translate: TranslateService
  ) {}

  async ngOnInit(): Promise<void> {
    this.profileForm = this.formBuilder.group({
      profiles: [[], Validators.required],
    })

    let profiles = await this.profileService.getProfiles();
    let potentialTeamProfiles = profiles.map(profile => {
      let teamProfile: TeamProfile = {
        team: { ...this.team, teamProfiles: undefined },
        profile: profile!,
      };
      return teamProfile;
    });
    this.teamProfileList = potentialTeamProfiles;
    this.teamProfileList = potentialTeamProfiles.filter(potentialTeamProfile =>
      !this.team.teamProfiles!.some(alreadyProfile =>
        alreadyProfile.profile!.profileId === potentialTeamProfile.profile!.profileId));
  }

  async onSave() {
    this.selectedTeamProfiles.forEach((teamProfile) => {
      let allocatedCost = this.calculateCostAllocation(teamProfile);
      let allocatedHours = this.calculateHourAllocation(teamProfile);
      teamProfile.allocatedCost = allocatedCost;
      teamProfile.allocatedHours = allocatedHours;
    });
    this.team.teamProfiles!.push(...this.selectedTeamProfiles);
    let DTO: TeamDTO = this.createDTO();
    console.log("team profiler i add-to-team-dialog: ", DTO.teamProfiles);
    try {
      const updatedTeam = await this.teamService.putTeam(DTO);

      if (updatedTeam != undefined) {
        this.AddToTeam.emit(updatedTeam);
        console.log("team profiler i add-to-team-dialog efter update: ", updatedTeam.teamProfiles);
        this.snackBar.openSnackBar(this.translate.instant('SUCCESS_TEAM_UPDATED'), true);
      } else {
        this.snackBar.openSnackBar(this.translate.instant('ERROR_TEAM_UPDATED'), false);
      }
    } catch (error: any) {
      console.error(error);
      this.snackBar.openSnackBar(this.translate.instant('ERROR_TEAM_UPDATED'), false);
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

  private createDTO(): TeamDTO {
    let teamProfiles = this.team.teamProfiles!.map(teamProfile => {
      let teamProfileDTO: TeamProfileDTO = {
        teamProfileId: teamProfile.teamProfileId!,
        team: this.team.teamId!,
        profile: teamProfile.profile!,
        allocationPercentageHours: teamProfile.allocationPercentageHours!,
        allocatedHours: teamProfile.allocatedHours!,
        allocationPercentageCost: teamProfile.allocationPercentageCost!,
        allocatedCost: teamProfile.allocatedCost!,
      };
      return teamProfileDTO;
    });
    let teamDTO: TeamDTO = {
      teamId: this.team.teamId,
      name: this.team.name!,
      markupPercentage: this.team.markupPercentage!,
      totalCostWithMarkup: this.team.totalCostWithMarkup!,
      grossMarginPercentage: this.team.grossMarginPercentage!,
      totalCostWithGrossMargin: this.team.totalCostWithGrossMargin!,
      hourlyRate: this.team.hourlyRate!,
      dayRate: this.team.dayRate!,
      totalAllocatedHours: this.team.totalAllocatedHours!,
      totalAllocatedCost: this.team.totalAllocatedCost!,
      updatedAt: this.team.updatedAt!,
      archived: this.team.archived!,
      teamProfiles: teamProfiles!,
      geographies: this.team.geographies!,
    };
    return teamDTO;
  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedTeamProfiles = $event.source.selectedOptions.selected.map(teamProfile =>
      teamProfile.value);
  }
}
