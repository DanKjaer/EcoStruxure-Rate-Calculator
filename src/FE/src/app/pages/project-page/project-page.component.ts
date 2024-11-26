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
import {Project, ProjectMembers} from "../../models";
import {ProjectService} from '../../services/project.service';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';
import {MenuService} from '../../services/menu.service';
import {SnackbarService} from '../../services/snackbar.service';
import {ActivatedRoute} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {AddToProjectDialogComponent} from '../../modals/add-to-project-dialog/add-to-project-dialog.component';

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
  selectedRow: ProjectMembers | null = null;

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
              private route: ActivatedRoute,
              private menuService: MenuService,
              private snackBar: SnackbarService,
              private translate: TranslateService,
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
    this.datasource.data = this.project.projectMembers;
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
      let updatedProject = {
        projectId: this.project.projectId,
        projectName: this.projectForm.value.projectName,
        projectSalesNumber: this.projectForm.value.salesNumber,
        projectPrice: this.projectForm.value.projectPrice,
        projectDescription: this.projectForm.value.projectDescription,
        projectStartDate: this.projectForm.value.startDate,
        projectEndDate: this.projectForm.value.endDate,
        projectLocation: this.project.projectLocation,
        projectMembers: this.project.projectMembers,
        projectDayRate: this.project.projectDayRate || 0
      };

      const response = await this.projectService.putProject(updatedProject);
      if (response != undefined) {
        this.project = response;
        this.fillTableWithTeams();
        this.fillStatBox();
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

  async onRemove(row: ProjectMembers) {
    this.selectedRow = row;
    this.project.projectMembers = this.project.projectMembers.filter(member => member.teamId !== row.teamId);
    const updatedProject = await this.projectService.deleteProjectMember(this.project, this.selectedRow?.teamId!);
    if (updatedProject) {
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_PROJECT_DELETED'), true);
      this.project = updatedProject;
      this.fillTableWithTeams();
      this.fillStatBox();
      this.calculateProjectDayRate();
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

    this.project.projectMembers.forEach(member => {
      if(member.teamId === selectedProject.teamId) {
        member.projectAllocation = selectedProject.projectAllocation;
      }
    });

    try{
      const updatedProject = {
        ...this.project,
        projectDayRate: this.project.projectDayRate
      }
      this.project = await this.projectService.putProject(updatedProject);
      this.project.projectMembers.forEach(member => {
        if(member.teamId === selectedProject.teamId) {
          member.dayRateWithMarkup = member.dayRate! * (member.markup! / 100 + 1) * (member.projectAllocation / 100);
        }
      });
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

  private calculateProjectDayRate(){
    let totalDayRate = 0;
    this.project.projectMembers.forEach(member => totalDayRate += member.dayRateWithMarkup!);
    this.project.projectDayRate = totalDayRate;
    this.fillStatBox();
  }
}
