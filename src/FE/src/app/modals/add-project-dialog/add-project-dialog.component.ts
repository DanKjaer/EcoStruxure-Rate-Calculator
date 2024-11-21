import {ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {MatDialogModule} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import {MatOption, MatSelect} from '@angular/material/select';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {MatNativeDateModule} from '@angular/material/core';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatInput} from '@angular/material/input';
import {Geography, Project, Team} from '../../models';
import {MatListOption, MatSelectionList, MatSelectionListChange} from '@angular/material/list';
import {
  MatDatepickerModule
} from '@angular/material/datepicker';
import { ChangeDetectorRef } from '@angular/core';
import {ProjectService} from '../../services/project.service';
import {SnackbarService} from '../../services/snackbar.service';
import {TeamsService} from '../../services/teams.service';
import {GeographyService} from '../../services/geography.service';

@Component({
  selector: 'app-add-project-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatSelect,
    TranslateModule,
    ReactiveFormsModule,
    MatButton,
    MatIcon,
    MatInput,
    MatOption,
    MatDialogModule,
    MatListOption,
    MatSelectionList,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './add-project-dialog.component.html',
  styleUrl: './add-project-dialog.component.css'
})
export class AddProjectDialogComponent implements OnInit {
  projectForm!: FormGroup;
  teamForm!: FormGroup;
  teamList: Team[] = [];
  locations!: Geography[];
  selectedProfiles: Team[] = [];
  @Output() projectAdded = new EventEmitter<Project>();


  constructor(private formBuilder: FormBuilder,
              private teamService: TeamsService,
              private projectService: ProjectService,
              public geographyService: GeographyService,
              private ChangeDetectorRef: ChangeDetectorRef,
              private snackBar: SnackbarService,
              private translate: TranslateService) {
  }

  async ngOnInit() {
    this.projectForm = this.formBuilder.group({
      projectName: ['', Validators.required],
      geography: ['', Validators.required],
      projectPrice: ['', Validators.required],
      projectGrossMargin: [''],
      startDate: new FormControl<Date | null>(null),
      endDate: new FormControl<Date | null>(null),
      projectDescription: ['']
    })

    this.teamForm = this.formBuilder.group({
      teams: [[], Validators.required]
    });

    this.teamList = await this.teamService.getTeams();
    this.getCountries();
    this.ChangeDetectorRef.detectChanges();
  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedProfiles = $event.source.selectedOptions.selected.map(team => team.value);
  }

  async getCountries() {
    this.locations = await this.geographyService.getCountries();
  }

  async onAddProject() {
    if (this.projectForm.valid) {
      const startDate = this.projectForm.value.startDate;
      const endDate = this.projectForm.value.endDate;

      // Convert the picked dates to UTC
      const startDateUtc = new Date(Date.UTC(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()));
      const endDateUtc = new Date(Date.UTC(endDate.getFullYear(), endDate.getMonth(), endDate.getDate()));

      let project = {
        projectName: this.projectForm.value.projectName,
        projectSalesNumber: '123456',
        projectDescription: this.projectForm.value.projectDescription,
        projectMembers: this.teamForm.value.teams,
        projectDayRate: 0,
        projectStartDate: startDateUtc,
        projectEndDate: endDateUtc,
        projectPrice: this.projectForm.value.projectPrice,
        projectLocation: this.projectForm.value.geography
      }
      const newProject = await this.projectService.postProject(project);
      if (newProject) {
        this.projectAdded.emit(newProject);
        this.snackBar.openSnackBar(this.translate.instant('SUCCESS_PROJECT_CREATED'), true);
      } else {
        this.snackBar.openSnackBar(this.translate.instant('ERROR_PROJECT_CREATED'), false);
      }
    }
  }
}
