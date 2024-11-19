import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Project, ProjectMembers, Team} from "../../models";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from "@angular/material/dialog";
import {FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
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
    MatDialogTitle,
    FormsModule
  ],
  templateUrl: './add-to-project-dialog.component.html',
  styleUrl: './add-to-project-dialog.component.css'
})
export class AddToProjectDialogComponent implements OnInit {
  teamForm!: FormGroup;
  teamList: Team[] = [];
  selectedTeams: Team[] = [];
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
      teamName: ['', Validators.required],
      teams: [[], Validators.required],
      projectAllocations: this.formBuilder.array([])
    })

    let teams = await this.teamService.getTeams();
    this.teamList = teams.filter(team =>
      !this.project.projectMembers.some(member =>
        member.teamId === team.teamId));
  }

  async onSave() {
    const projectAllocations = this.teamForm.get('projectAllocations')?.value;
    let teams = this.selectedTeams;
    teams.forEach( (team, projectAllocationsIndex) => {
      let projectMember: ProjectMembers = {
        projectId: '',
        teamId: team.teamId!,
        name: team.name,
        dayRate: team.dayRate,
        markup: team.markup,
        projectAllocation: projectAllocations[projectAllocationsIndex]  || 0
      }
      this.project.projectMembers.push(projectMember);
    });
    const updateProject = await this.projectService.putProject(this.project)
    if (updateProject != undefined) {
      this.AddToProject.emit(updateProject);
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_TEAM_ADDED'), true);
    } else {
      this.snackBar.openSnackBar(this.translate.instant('ERROR_TEAM_ADDED'), false);
    }
  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedTeams = $event.source.selectedOptions.selected.map(team => team.value);
    const projectAllocations = this.teamForm.get('projectAllocations') as FormArray;
    projectAllocations.clear();
    this.selectedTeams.forEach(() => {
      projectAllocations.push(this.formBuilder.control(0, [Validators.required, Validators.min(0), Validators.min(100)]));
    });
  }
}
