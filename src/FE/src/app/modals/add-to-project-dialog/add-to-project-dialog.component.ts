import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Project, ProjectMembers, Team} from "../../models";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from "@angular/material/dialog";
import {FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {MatList, MatListItem, MatListOption, MatSelectionList, MatSelectionListChange} from "@angular/material/list";
import {MatFormField, MatInputModule} from "@angular/material/input";
import {TeamsService} from '../../services/teams.service';
import {ProjectService} from '../../services/project.service';
import {SnackbarService} from '../../services/snackbar.service';
import {MatLine} from '@angular/material/core';

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
    MatDialogTitle,
    FormsModule,
    MatList,
    MatListItem,
    MatLine
  ],
  templateUrl: './add-to-project-dialog.component.html',
  styleUrl: './add-to-project-dialog.component.css'
})
export class AddToProjectDialogComponent implements OnInit {
  teamForm!: FormGroup;
  teamList: ProjectMembers[] = [];
  selectedTeams: ProjectMembers[] = [];
  @Output() AddToProject = new EventEmitter<Project>();
  @Input() project!: Project;

  constructor(
      private formBuilder: FormBuilder,
      private teamService: TeamsService,
      private projectService: ProjectService,
      private snackBar:SnackbarService,
      private translate: TranslateService
  ) {}

  async ngOnInit() {
    this.teamForm = this.formBuilder.group({
      teams: [[], Validators.required],
    })

    let teams = await this.teamService.getTeams();
    let potentialProjectMembers = teams.map(team => {
      let projectMember: ProjectMembers = {
        projectId: this.project.projectId!,
        teamId: team.teamId!,
        name: team.name,
        dayRate: team.dayRate,
        markup: team.markup,
        projectAllocation: 0
      }
      return projectMember;
    });
    this.teamList = potentialProjectMembers.filter(potentialProjectMember =>
      !this.project.projectMembers.some(alreadyMember =>
        alreadyMember.teamId === potentialProjectMember.teamId));
  }

  async onSave() {
    let teams = this.selectedTeams;
    teams.forEach( (team) => {
      let projectMember: ProjectMembers = {
        projectId: '',
        teamId: team.teamId!,
        name: team.name,
        dayRate: team.dayRate,
        markup: team.markup,
        projectAllocation: team.projectAllocation
      }
      this.project.projectMembers.push(projectMember);
    });
    const updateProject = await this.projectService.putProject(this.project)
    if (updateProject != undefined) {
      this.AddToProject.emit(updateProject);
      this.project = updateProject;
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_TEAM_ADDED'), true);
    } else {
      this.snackBar.openSnackBar(this.translate.instant('ERROR_TEAM_ADDED'), false);
    }
  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedTeams = $event.source.selectedOptions.selected.map(team => team.value);
  }
}
