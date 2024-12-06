import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Profile, Team, TeamProfile} from '../../models';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
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
    FormsModule
  ],
  templateUrl: './add-profile-to-team-dialog.component.html',
  styleUrl: './add-profile-to-team-dialog.component.css'
})
export class AddProfileToTeamDialogComponent implements OnInit{
  @Output() addedProfileToTeam = new EventEmitter<TeamProfile>();
  @Input() profile!: Profile;
  @Input() teamProfiles!: TeamProfile[];
  teamList: Team[] = [];
  selectedTeams: Team[] = [];


  constructor(
    private teamService: TeamsService,
    private snackBar: SnackbarService,
    private translate: TranslateService
  ) {}

   async ngOnInit(): Promise<void> {

    let teams = await this.teamService.getTeams();
    this.teamList = teams.filter(potentialTeam => {
      return this.teamProfiles!.some(teamProfile =>
        teamProfile.team!.teamId !== potentialTeam.teamId);
    });
  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedTeams = $event.source.selectedOptions.selected.map(team =>
      team.value);
  }

  onSave() {

  }
}
