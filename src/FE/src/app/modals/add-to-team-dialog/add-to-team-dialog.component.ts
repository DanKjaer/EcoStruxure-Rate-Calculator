import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from '@angular/material/dialog';
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
import {NgIf} from "@angular/common";
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
  ],
  templateUrl: './add-to-team-dialog.component.html',
  styleUrl: './add-to-team-dialog.component.css'
})
export class AddToTeamDialogComponent implements OnInit {
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
    private generateDTOService: GenerateDTOService
  ) {
  }

  async ngOnInit(): Promise<void> {
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

  async onSave() {
    this.selectedTeamProfiles.forEach((teamProfile) => {
      let allocatedCost = this.calculationsService.calculateCostAllocation(teamProfile);
      let allocatedHours = this.calculationsService.calculateHourAllocation(teamProfile);
      teamProfile.allocatedCost = allocatedCost;
      teamProfile.allocatedHours = allocatedHours;
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
  }
}
