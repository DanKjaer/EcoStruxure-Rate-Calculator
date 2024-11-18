import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Project, ProjectMembers, Team} from "../../models";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from "@angular/material/dialog";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {MatListOption, MatSelectionList, MatSelectionListChange} from "@angular/material/list";
import {MatFormField, MatInputModule} from "@angular/material/input";
import {TeamsService} from '../../services/teams.service';
import {ProjectService} from '../../services/project.service';
import {SnackbarService} from '../../services/snackbar.service';

@Component({
  selector: 'app-add-to-project-dialog',
  standalone: true,
  imports: [
    TranslateModule,
    MatDialogContent,
    MatFormField,
    ReactiveFormsModule,
    MatIcon,
    MatSelectionList,
    MatListOption,
    MatDialogActions,
    MatDialogClose,
    MatInputModule,
    MatDialogTitle
  ],
  templateUrl: './add-to-project-dialog.component.html',
  styleUrl: './add-to-project-dialog.component.css'
})
export class AddToProjectDialogComponent implements OnInit {
  teamForm!: FormGroup;
  teamList: Team[] = [];
  selectedTeams: Team[] = [];
  @Output() AddToProject = new EventEmitter<Project>();

  constructor(
      private formBuilder: FormBuilder,
      private teamService: TeamsService,
      private projectService: ProjectService,
      private snackBar:SnackbarService,
      private translate: TranslateService
  ) {}

  async ngOnInit() {
    this.teamForm = this.formBuilder.group({
      teamName: ['', Validators.required],
      teams: [[], Validators.required]
    })

    this.teamList = await this.teamService.getTeams();
  }

  async onSave() {

    let teams = this.selectedTeams;
    let projectMembers : ProjectMembers[] = [];
    teams.forEach( team => {
      let projectMember: ProjectMembers = {
        projectId: '',
        teamId: team.teamId!,
        name: team.name,
        dayRate: team.dayRate,
        markup: team.markup,
        projectAllocation: 0

      }
      projectMembers.push(projectMember);
    });
    const updateTeam = await this.projectService.putProject(projectMembers)
    if (updateTeam != undefined) {
      this.AddToProject.emit(updateTeam);
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_TEAM_ADDED'), true);
    } else {
      this.snackBar.openSnackBar(this.translate.instant('ERROR_TEAM_ADDED'), false);
    }
  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedTeams = $event.source.selectedOptions.selected.map(team => team.value);
  }
}
