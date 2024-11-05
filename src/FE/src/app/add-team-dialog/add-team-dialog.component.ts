import {booleanAttribute, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {MatDialogModule} from '@angular/material/dialog';
import {TranslateModule} from '@ngx-translate/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {MatFormField, MatLabel, MatPrefix} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatListModule, MatSelectionListChange} from '@angular/material/list';
import {TeamsService} from '../services/teams.service';
import {ProfileService} from '../services/profile.service';
import {Profile, Team, TeamProfiles} from '../models';

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
    MatPrefix
  ],
  templateUrl: './add-team-dialog.component.html',
  styleUrl: './add-team-dialog.component.css'
})
export class AddTeamDialogComponent implements OnInit {
  teamForm!: FormGroup;
  profileList: Profile[] = [];
  selectedProfiles: Profile[] = [];
  @Output() teamAdded = new EventEmitter<Team>();

  constructor(private fb: FormBuilder, private teamService: TeamsService, private profileService: ProfileService) {
  }

  async ngOnInit() {
    this.teamForm = this.fb.group({
      name: ['', Validators.required],
      profiles: [[], Validators.required]
    })

    this.profileList = await this.profileService.getProfiles();
  }

  async onSave() {
    let team: Team = {
      name : this.teamForm.value.name,
    };
    let profiles = this.selectedProfiles;
    let teamProfiles :TeamProfiles[] = [];
    profiles.forEach(profile => {
      let teamProfile: TeamProfiles = {
        profileId : profile.profileId!,
        name : profile.name,
        annualCost : profile.annualCost!,
        annualHours : profile.annualHours!,
        costAllocation : 100,
        hourAllocation : 100,
      };
      teamProfiles.push(teamProfile);
    });
    const newTeam = await this.teamService.postTeam(team, teamProfiles);
    this.teamAdded.emit(newTeam);
  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedProfiles = $event.source.selectedOptions.selected.map(profile => profile.value);
  }
}
