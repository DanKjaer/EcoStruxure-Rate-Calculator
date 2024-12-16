import {ChangeDetectorRef, Component, computed, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {DecimalPipe, NgClass, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatTableModule, MatTableDataSource} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {MatFormField, MatInput, MatPrefix} from "@angular/material/input";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {MatLabel} from "@angular/material/form-field";
import {Project, ProjectTeam} from "../../models";
import {ProjectService} from '../../services/project.service';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';
import {MenuService} from '../../services/menu.service';
import {SnackbarService} from '../../services/snackbar.service';
import {ActivatedRoute} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {AddToProjectDialogComponent} from '../../modals/add-to-project-dialog/add-to-project-dialog.component';
import {CalculationsService} from '../../services/calculations.service';
import {SearchConfigService} from '../../services/search-config.service';

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
  project: WritableSignal<Project | null> = signal<Project | null>(null);
  originalRowData: { [key: number]: any } = {};
  isEditingRow: boolean = false;
  selectedRow: ProjectTeam | null = null;

  loading: boolean = true;
  isMenuOpen: boolean | undefined;
  datasource: MatTableDataSource<any> = new MatTableDataSource();
  displayedColumns: string[] = [
    'projectName',
    'projectAllocation',
    'dayRate',
    'options'
  ];

  statBoxes = computed(() => {
    const project = this.project()!;

    if (!project) {
      return {totalDayRate: 0, totalPrice: 0, grossMargin: 0, totalDays: 0};
    }
    const totalDayRate = project.projectDayRate!;
    const totalPrice = project.projectPrice!;
    const grossMargin = project.projectGrossMargin!;
    const totalDays = project.projectTotalDays!;

    return {totalDayRate, totalPrice, grossMargin, totalDays};
  });

  constructor(protected calculationsService: CalculationsService,
              private projectService: ProjectService,
              private menuService: MenuService,
              private snackbarService: SnackbarService,
              private translateService: TranslateService,
              private searchConfigService: SearchConfigService,
              private formBuilder: FormBuilder,
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

    this.project.set(await this.projectService.getProject(this.route.snapshot.paramMap.get('id')!));
    this.fillTableWithTeams();
    this.fillProjectForm();
    this.searchConfigService.configureFilter(this.datasource, ['team.name']);
    this.loading = false;
  }

  private fillTableWithTeams() {
    this.datasource.data = this.project()!.projectTeams;
    this.datasource._updateChangeSubscription();
  }

  private fillProjectForm() {
    this.projectForm = this.formBuilder.group({
      projectName: [this.project()!.projectName, Validators.required],
      salesNumber: [this.project()!.projectSalesNumber,Validators.required],
      projectPrice: [this.project()!.projectPrice, Validators.required],
      projectDescription: [this.project()!.projectDescription],
      startDate: [this.project()!.projectStartDate, Validators.required],
      endDate: [this.project()!.projectEndDate, Validators.required]
    });
  }

  openDialog() {
    const dialogRef = this.dialog.open(AddToProjectDialogComponent);
    this.loading = true;
    dialogRef.componentInstance.project = this.project()!;
    dialogRef.componentInstance.AddToProject.subscribe((project: Project) => {
      this.project.set(project);
      this.fillTableWithTeams();
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
      let updatedProject: Project = {
        projectId: this.project()!.projectId,
        projectLocation: this.project()!.projectLocation,
        projectTeams: this.project()!.projectTeams,
        projectName: this.projectForm.value.projectName,
        projectSalesNumber: this.projectForm.value.salesNumber,
        projectDescription: this.projectForm.value.projectDescription,
        projectPrice: this.projectForm.value.projectPrice,
        projectStartDate: this.projectForm.value.startDate,
        projectEndDate: this.projectForm.value.endDate,
        projectDayRate: this.project()!.projectDayRate
      }
      this.project.set(await this.projectService.putProject(updatedProject));

      if (this.project != undefined) {
        this.fillProjectForm();
        this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_PROJECT_UPDATED'), true);
      } else {
        this.snackbarService.openSnackBar(this.translateService.instant('ERROR_PROJECT_UPDATED'), false);
      }
    }
  }

  undo() {
    this.projectForm = this.formBuilder.group({
      projectName: [this.project()!.projectName],
      salesNumber: [this.project()!.projectSalesNumber],
      projectPrice: [this.project()!.projectPrice],
      projectDescription: [this.project()!.projectDescription],
      startDate: [this.project()!.projectStartDate],
      endDate: [this.project()!.projectEndDate]
    });
  }

  async onRemove() {
    const result = await this.projectService.deleteProjectMember(this.selectedRow!.projectTeamId!,
      this.project()!.projectId!);
    if (result) {
      this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_PROJECT_DELETED'), true);
      this.project.set(await this.projectService.getProject(this.project()!.projectId!));
      this.fillTableWithTeams();
    } else {
      this.snackbarService.openSnackBar(this.translateService.instant('ERROR_PROJECT_DELETED'), false);
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

    this.project()!.projectTeams.forEach(member => {
      if (member.team.teamId === selectedProject.teamId) {
        member.allocationPercentage = selectedProject.allocationPercentage;
      }
    });

    try {
      const updatedProject = {
        ...this.project()!,
        projectDayRate: this.project()!.projectDayRate
      }
      this.project.set(await this.projectService.putProject(updatedProject));
      this.fillTableWithTeams();
      this.fillProjectForm();
      this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_PROJECT_SAVED'), true);
      this.loading = false;
    } catch (e) {
      this.cancelEdit(selectedProject);
      this.snackbarService.openSnackBar(this.translateService.instant('ERROR_PROJECT_SAVED'), false);
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
