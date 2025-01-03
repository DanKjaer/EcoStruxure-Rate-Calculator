import {ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {MatDialogModule} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatOption, MatSelect} from '@angular/material/select';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {MatNativeDateModule} from '@angular/material/core';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatInput} from '@angular/material/input';
import {Geography, Project, ProjectTeam, Team} from '../../models';
import {MatListModule, MatSelectionListChange} from '@angular/material/list';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {ChangeDetectorRef} from '@angular/core';
import {ProjectService} from '../../services/project.service';
import {SnackbarService} from '../../services/snackbar.service';
import {TeamsService} from '../../services/teams.service';
import {GeographyService} from '../../services/geography.service';
import {MatDivider} from '@angular/material/divider';

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
    MatListModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
    MatDivider,
    FormsModule,
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
  selectedProjectTeam: ProjectTeam[] = [];
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
      projectSalesNumber: [''],
      projectGrossMargin: [''],
      startDate: new FormControl<Date | null>(null),
      endDate: new FormControl<Date | null>(null),
      projectDescription: ['']
    })

    this.teamForm = this.formBuilder.group({
      allocations: this.formBuilder.array([])
    });

    this.teamList = await this.teamService.getTeams();
    this.getCountries();
    this.ChangeDetectorRef.detectChanges();
  }

  isSaveDisabled(): boolean {
    return this.teamForm.invalid || this.selectedProjectTeam.length === 0 && this.projectForm.invalid;
  }

  get allocations(): FormArray {
    return this.teamForm.get('allocations') as FormArray;
  }

  getFormGroupAt(index: number): FormGroup {
    return this.allocations.at(index) as FormGroup;
  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedProjectTeam = $event.source.selectedOptions.selected.map(team => {
      let selectedTeam: Team = team.value
      let projectTeam: ProjectTeam = {
        team: selectedTeam,
      };
      return projectTeam;
    });

    this.allocations.clear();

    this.selectedProjectTeam.forEach((projectTeam) => {
      this.allocations.push(
        this.formBuilder.group({
          allocation: [
            null,
            [Validators.required, Validators.min(0), Validators.max(100)]
          ]
        })
      );
    });
  }

  async getCountries() {
    this.locations = await this.geographyService.getCountries();
  }

  async onAddProject() {
    const startDate = this.projectForm.value.startDate;
    const endDate = this.projectForm.value.endDate;

    // Convert the picked dates to UTC
    const startDateUtc = new Date(Date.UTC(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()));
    const endDateUtc = new Date(Date.UTC(endDate.getFullYear(), endDate.getMonth(), endDate.getDate()));

    this.selectedProjectTeam.forEach((projectTeam, index) => {
      const allocationForm = this.allocations.at(index) as FormGroup;
      projectTeam.allocationPercentage = allocationForm.get('allocation')!.value;
    });

    let project = {
      projectName: this.projectForm.value.projectName,
      projectSalesNumber: this.projectForm.value.projectSalesNumber,
      projectDescription: this.projectForm.value.projectDescription,
      projectTeams: this.selectedProjectTeam,
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
