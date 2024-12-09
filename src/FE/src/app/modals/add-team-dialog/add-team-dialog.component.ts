import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {MatDialogModule} from '@angular/material/dialog';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {
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
import {NgIf} from '@angular/common';

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
    NgIf
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
    private fb: FormBuilder
    , private teamService: TeamsService
    , private profileService: ProfileService
    , private snackBar: SnackbarService
    , private translate: TranslateService) {
  }

  async ngOnInit() {
    this.teamForm = this.fb.group({
      name: ['', Validators.required],
      profiles: [[], Validators.required]
    })
    this.profileList = await this.profileService.getProfiles();
    this.profileList.sort((a, b) => {
      return a.name.localeCompare(b.name);
    });
  }

  async onSave() {
    let team: Team = {
      name : this.teamForm.value.name,
      teamProfiles: this.selectedProfiles,
      markupPercentage: 0,
      grossMarginPercentage: 0
    };
    console.log(team);
    const newTeam = await this.teamService.postTeam(team);
    if (newTeam.teamId != undefined) {
      this.teamAdded.emit(newTeam);
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_TEAM_CREATED'), true);
    } else {
      this.snackBar.openSnackBar(this.translate.instant('ERROR_TEAM_CREATED'), false);
    }
  }

  onSelectionChange($event: MatSelectionListChange) {
    let teamProfiles: TeamProfile[] = [];
    $event.source.selectedOptions.selected.map(profile => {
      let teamProfile: TeamProfile = {
        profile: profile.value
      };
      teamProfiles.push(teamProfile);
    });
    this.selectedProfiles = teamProfiles;
    console.log(this.selectedProfiles);
  }
}
