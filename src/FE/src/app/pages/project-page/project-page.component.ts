import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {DecimalPipe, NgClass, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import { MatTableModule, MatTableDataSource } from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {MatFormField, MatInput, MatPrefix} from "@angular/material/input";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {MatLabel} from "@angular/material/form-field";
import {Project, ProjectTeam, Team} from "../../models";
import {ProjectService} from '../../services/project.service';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';
import {MenuService} from '../../services/menu.service';
import {SnackbarService} from '../../services/snackbar.service';
import {ActivatedRoute} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {AddToProjectDialogComponent} from '../../modals/add-to-project-dialog/add-to-project-dialog.component';
import {CalculationsService} from '../../services/calculations.service';

@Component({
  selector: 'app-project-page',
  standalone: true,
  imports: [
    DecimalPipe,
    FormsModule,
    MatButton,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatMenu,
    MatMenuItem,
    MatProgressSpinner,
    MatTableModule,
    NgIf,
    TranslateModule,
    NgClass,
    MatFormField,
    MatMenuTrigger,
    ReactiveFormsModule,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatDatepicker,
    MatPrefix
  ],
  templateUrl: './project-page.component.html',
  styleUrl: './project-page.component.css'
})
export class ProjectPageComponent implements OnInit {
  readonly dialog = inject(MatDialog);
  projectForm: FormGroup = new FormGroup({});
  project!: Project;
  originalRowData: { [key: number]: any } = {};
  isEditingRow: boolean = false;
  selectedRow: ProjectTeam | null = null;

  statBoxes = {
    totalDayRate: 0,
    totalPrice: 0,
    grossMargin: 0,
    totalDays: 0
  };
  loading: boolean = true;
  isMenuOpen: boolean | undefined;
  datasource: MatTableDataSource<any> = new MatTableDataSource();
  displayedColumns: string[] = [
    'projectName',
    'projectAllocation',
    'dayRate',
    'options'
  ];


  constructor(private formBuilder: FormBuilder,
              private projectService: ProjectService,
              private menuService: MenuService,
              private snackBar: SnackbarService,
              private translate: TranslateService,
              protected calculationsService: CalculationsService,
              private route: ActivatedRoute,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  async ngOnInit(): Promise<void> {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    });

    this.projectForm = this.formBuilder.group({
      projectName: ['', Validators.required],
      salesNumber: ['', Validators.required],
      projectPrice: ['', Validators.required],
      projectDescription: [''],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    });

    this.project = await this.projectService.getProject(this.route.snapshot.paramMap.get('id')!);
    this.fillTableWithTeams();
    this.loading = false;
    this.fillStatBox();
    this.fillProjectForm();
  }

  private fillTableWithTeams() {
    this.datasource.data = this.project.projectTeams;
    this.datasource._updateChangeSubscription();
  }

  private fillStatBox() {
    this.statBoxes = {
      totalDayRate: this.project.projectDayRate!,
      totalPrice: this.project.projectPrice!,
      grossMargin: this.project.projectGrossMargin!,
      totalDays: this.project.projectTotalDays!
    }
  }

  private fillProjectForm() {
    this.projectForm = this.formBuilder.group({
      projectName: [this.project.projectName, Validators.required],
      salesNumber: [this.project.projectSalesNumber],
      projectPrice: [this.project.projectPrice],
      projectDescription: [this.project.projectDescription],
      startDate: [this.project.projectStartDate],
      endDate: [this.project.projectEndDate],
      dayRate: [this.project.projectDayRate || 0]
    });
  }

  openDialog() {
    const dialogRef = this.dialog.open(AddToProjectDialogComponent);
    this.loading = true;
    dialogRef.componentInstance.project = this.project;
    dialogRef.componentInstance.AddToProject.subscribe((project: Project) => {
      this.project = project;
      this.fillTableWithTeams();
      this.fillStatBox();
    });
    this.loading = false;
    this.changeDetectorRef.detectChanges();
  }

  applySearch(event: Event) {
    const searchValue = (event.target as HTMLInputElement).value;
    this.datasource.filter = searchValue.trim().toLowerCase();

    if (this.datasource.paginator) {
      this.datasource.paginator.firstPage();
    }
  }

  async update() {
    if (this.projectForm.valid) {
      this.project.projectName = this.projectForm.value.projectName;
      this.project.projectSalesNumber = this.projectForm.value.salesNumber;
      this.project.projectDescription = this.projectForm.value.projectDescription;
      this.project.projectPrice = this.projectForm.value.projectPrice;
      this.project.projectStartDate = this.projectForm.value.startDate;
      this.project.projectEndDate = this.projectForm.value.endDate;
      this.project = await this.projectService.putProject(this.project);
      if (this.project != undefined) {
        this.fillStatBox();
        this.fillProjectForm();
        this.snackBar.openSnackBar(this.translate.instant('SUCCESS_PROJECT_UPDATED'), true);
      } else {
        this.snackBar.openSnackBar(this.translate.instant('ERROR_PROJECT_UPDATED'), false);
      }
    }
  }

  undo() {
    this.projectForm = this.formBuilder.group({
      projectName: [this.project.projectName],
      salesNumber: [this.project.projectSalesNumber],
      projectPrice: [this.project.projectPrice],
      projectDescription: [this.project.projectDescription],
      startDate: [this.project.projectStartDate],
      endDate: [this.project.projectEndDate]
    });
  }

  async onRemove() {
    const result = await this.projectService.deleteProjectMember(this.selectedRow!.projectTeamId!, this.project.projectId!);
    if (result) {
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_PROJECT_DELETED'), true);
      this.project = await this.projectService.getProject(this.project.projectId!);
      this.fillTableWithTeams();
      this.fillStatBox();
    } else {
      this.snackBar.openSnackBar(this.translate.instant('ERROR_PROJECT_DELETED'), false);
    }
  }

  editRow(selectedProject: any) {
    if (this.isEditingRow) return;
    this.isEditingRow = true;
    selectedProject['isEditing'] = true;
    if (!this.originalRowData[selectedProject.id]) {
      this.originalRowData[selectedProject.id] = {...selectedProject}
    }
  }

  async saveEdit(selectedProject: any): Promise<void> {
    selectedProject['isEditing'] = false;
    this.isEditingRow = false;
    this.loading = true;

    this.project.projectTeams.forEach(member => {
      if(member.team.teamId === selectedProject.teamId) {
        member.allocationPercentage = selectedProject.allocationPercentage;
      }
    });

    try{
      const updatedProject = {
        ...this.project,
        projectDayRate: this.project.projectDayRate
      }
      this.project = await this.projectService.putProject(updatedProject);
      this.fillTableWithTeams();
      this.fillProjectForm();
      this.fillStatBox();
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_PROJECT_SAVED'), true);
      this.loading = false;
    } catch (e) {
      this.cancelEdit(selectedProject);
      this.snackBar.openSnackBar(this.translate.instant('ERROR_PROJECT_SAVED'), false);
      this.loading = false;
      return;
    }
  }

  cancelEdit(selectedProject: any) {
    let original = this.originalRowData[selectedProject.id];
    if (original) {
      selectedProject.projectAllocation = original.projectAllocation;
    }
    selectedProject['isEditing'] = false;
    this.isEditingRow = false;
  }

  selectRow(row: ProjectTeam) {
    this.selectedRow = row;
  }
}
