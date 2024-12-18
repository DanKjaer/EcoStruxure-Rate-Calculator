import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Project, ProjectTeam} from "../../models";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from "@angular/material/dialog";
import {FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {MatListModule, MatSelectionListChange} from "@angular/material/list";
import {MatFormField, MatInputModule} from "@angular/material/input";
import {TeamsService} from '../../services/teams.service';
import {ProjectService} from '../../services/project.service';
import {SnackbarService} from '../../services/snackbar.service';
import {MatButton} from '@angular/material/button';
import {MatDivider} from '@angular/material/divider';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-add-to-project-dialog',
  standalone: true,
  imports: [
    TranslateModule,
    MatDialogContent,
    MatFormField,
    ReactiveFormsModule,
    MatIcon,
    MatListModule,
    MatDialogActions,
    MatDialogClose,
    MatInputModule,
    MatDialogTitle,
    FormsModule,
    MatButton,
    MatDivider,
    NgForOf,
    NgIf
  ],
  templateUrl: './add-to-project-dialog.component.html',
  styleUrl: './add-to-project-dialog.component.css'
})
export class AddToProjectDialogComponent implements OnInit {
  teamForm!: FormGroup;
  teamList: ProjectTeam[] = [];
  selectedTeams: ProjectTeam[] = [];
  @Output() AddToProject = new EventEmitter<Project>();
  @Input() project!: Project;

  constructor(
    private formBuilder: FormBuilder,
    private teamService: TeamsService,
    private projectService: ProjectService,
    private snackBar: SnackbarService,
    private translate: TranslateService
  ) {
  }

  async ngOnInit() {
    this.teamForm = this.formBuilder.group({
      allocations: this.formBuilder.array([]),
    })

    let teams = await this.teamService.getTeams();
    let potentialProjectMembers = teams.map(team => {
      let projectMember: ProjectTeam = {
        project: this.project!,
        team: team!,
        allocationPercentage: 0
      }
      return projectMember;
    });
    this.teamList = potentialProjectMembers.filter(potentialProjectMember =>
      !this.project.projectTeams.some(alreadyMember =>
        alreadyMember.team.teamId === potentialProjectMember.team.teamId));
  }

  isSaveDisabled(): boolean {
    return this.teamForm.invalid || this.selectedTeams.length === 0;
  }

  get allocations(): FormArray {
    return this.teamForm.get('allocations') as FormArray;
  }

  getFormGroupAt(index: number): FormGroup {
    return this.allocations.at(index) as FormGroup;
  }

  async onSave() {
    let teams = this.selectedTeams;
    teams.forEach((team, index) => {
      const allocationForm = this.allocations.at(index) as FormGroup;
      let projectTeam: ProjectTeam = {
        projectTeamId: '',
        project: this.project.projectId,
        team: team.team!,
        allocationPercentage: allocationForm.get('allocation')!.value
      }
      this.project.projectTeams.push(projectTeam);
    });
    try {
      const updateProject = await this.projectService.putProject(this.project)
      if (updateProject != undefined) {
        this.AddToProject.emit(updateProject);
        this.project = updateProject;
        this.snackBar.openSnackBar(this.translate.instant('SUCCESS_TEAM_ADDED'), true);
      } else {
        this.snackBar.openSnackBar(this.translate.instant('ERROR_TEAM_ADDED'), false);
      }
    } catch (error: any) {
      this.snackBar.openSnackBar(this.translate.instant('ERROR_PROJECT_UPDATED'), false);
    }
  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedTeams = $event.source.selectedOptions.selected.map(team => team.value);

    this.allocations.clear();

    this.selectedTeams.forEach((team) => {
      this.allocations.push(
        this.formBuilder.group({
          allocation: [
            null,
            [Validators.required, Validators.min(0), Validators.max(100)]
          ]
        })
      )
    });
  }
}
