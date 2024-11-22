import {AfterViewInit, ChangeDetectorRef, Component, inject, OnInit, ViewChild} from '@angular/core';
import {MatButton, MatIconButton} from '@angular/material/button';
import {
  MatCell,
  MatCellDef,
  MatColumnDef, MatFooterCell, MatFooterCellDef, MatFooterRow, MatFooterRowDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource
} from '@angular/material/table';
import {MatIcon} from '@angular/material/icon';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort, MatSortHeader, Sort} from '@angular/material/sort';
import {DecimalPipe, NgClass, NgIf} from '@angular/common';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {MatDialog} from '@angular/material/dialog';
import {Project} from '../../models';
import {ProjectService} from '../../services/project.service';
import {FormatterService} from '../../services/formatter.service';
import {AddProjectDialogComponent} from '../../modals/add-project-dialog/add-project-dialog.component';
import {SnackbarService} from '../../services/snackbar.service';
import {MenuService} from '../../services/menu.service';
import {Router} from '@angular/router';
import {MatFormField, MatInput, MatLabel, MatPrefix, MatSuffix} from '@angular/material/input';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';
import {CurrencyService} from '../../services/currency.service';


@Component({
  selector: 'app-projects-page',
  standalone: true,
  imports: [
    MatButton,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatIconButton,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger,
    MatPaginator,
    MatRow,
    MatRowDef,
    MatSort,
    MatSortHeader,
    MatTable,
    NgIf,
    TranslateModule,
    MatHeaderCellDef,
    NgClass,
    MatInput,
    ReactiveFormsModule,
    FormsModule,
    MatDatepickerToggle,
    MatFormField,
    MatLabel,
    MatSuffix,
    MatDatepickerInput,
    MatDatepicker,
    DecimalPipe,
    MatFooterRow,
    MatFooterCell,
    MatFooterCellDef,
    MatFooterRowDef,
    MatPrefix
  ],
  templateUrl: './projects-page.component.html',
  styleUrl: './projects-page.component.css'
})
export class ProjectsPageComponent implements AfterViewInit, OnInit {
  readonly dialog = inject(MatDialog);

  displayedColumns: string[] = ['name',
                                'salesNumber',
                                'members',
                                'dayRate',
                                'grossMargin',
                                'price',
                                'startDate',
                                'endDate',
                                'totalDays',
                                'location',
                                'options'];

  protected readonly localStorage = localStorage;

  selectedRow: Project | null = null;
  originalRowData: { [key: number]: any } = {};
  datasource: MatTableDataSource<Project> = new MatTableDataSource<Project>();
  loading = true;
  isMenuOpen: boolean | undefined;
  isEditingRow: boolean = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  totalDays: number = 0;
  totalPrice: number = 0;
  totalDayRate: number = 0;

  constructor(private projectService: ProjectService,
              private formatter: FormatterService,
              private snackBar: SnackbarService,
              private ChangeDetectorRef: ChangeDetectorRef,
              private translate: TranslateService,
              private menuService: MenuService,
              private router: Router,
              protected currencyService: CurrencyService) {}

  async ngOnInit(): Promise<void> {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    });
    this.loading = true;
    try {
      const projects = await this.projectService.getProjects();
      projects.forEach(project => {
        project.startDateString = this.formatter.formatDate(project.projectStartDate);
        project.endDateString = this.formatter.formatDate(project.projectEndDate);

        project.projectMembersString = project.projectMembers.map(member => member.name).join(', ');
      });
      this.datasource.data = projects;

    } catch (error) {
      console.error('Failed to load projects:', error);
    } finally {
      this.loading = false;
      this.updateTableFooterData();
    }
  }

  async ngAfterViewInit(): Promise<void> {
    this.datasource.sort = this.sort;
    this.datasource.paginator = this.paginator;
    this.updateTableFooterData();
  }

  applySearch(event: Event) {
    const searchValue = (event.target as HTMLInputElement).value;
    this.datasource.filter = searchValue.trim().toLowerCase();

    if (this.datasource.paginator) {
      this.datasource.paginator.firstPage();
    }
    this.updateTableFooterData(true);
  }

  openDialog() {
    const dialogRef = this.dialog.open(AddProjectDialogComponent, {
      minHeight: '80vh',
      maxHeight: '800px',
      minWidth: '60vw',
      maxWidth: '1200px',
    });
    this.loading = true;
    dialogRef.componentInstance.projectAdded.subscribe((project: Project) => {
      project.startDateString = this.formatter.formatDate(project.projectStartDate);
      project.endDateString = this.formatter.formatDate(project.projectEndDate);
      project.projectMembersString = project.projectMembers.map(member => member.name).join(', ');
      this.datasource.data.push(project);
      this.datasource._updateChangeSubscription();
    });
    this.updateTableFooterData();
    this.loading = false;
    this.ChangeDetectorRef.detectChanges();
  }

  async onDelete() {
    const result = await this.projectService.deleteProject(this.selectedRow?.projectId!);
    if (result) {
      this.datasource.data = this.datasource.data.filter((project: Project) => project.projectId !== this.selectedRow?.projectId);
      this.datasource._updateChangeSubscription();
      this.updateTableFooterData();
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_PROJECT_DELETED'), true);
    } else {
      this.snackBar.openSnackBar(this.translate.instant('ERROR_PROJECT_DELETED'), false);
    }
  }

  selectRow(row: Project) {
    this.selectedRow = row;
  }


  goToProject(projectId: string): void {
    this.router.navigate(['/projects', projectId]);
  }

  editRow(element: any): void {
    if (this.isEditingRow) return;
    this.isEditingRow = true;
    element['isEditing'] = true;
    if (!this.originalRowData[element.id]) {
      this.originalRowData[element.id] = {...element}
    }
  }

  async saveEdit(selectedProject: any) {
    selectedProject['isEditing'] = false;
    this.loading = true;
    this.isEditingRow = false;

    const endDate: Date = new Date(selectedProject.projectEndDate);
    selectedProject.projectEndDate = new Date(Date.UTC(endDate.getFullYear(), endDate.getMonth(), endDate.getDate()));
    try {
      let response = await this.projectService.putProject(selectedProject);
      this.datasource.data.forEach((project: Project) => {
        if (project.projectId === response.projectId) {
          project.projectName = response.projectName;
          project.projectGrossMargin = response.projectGrossMargin;
          project.projectPrice = response.projectPrice;
          project.projectEndDate = response.projectEndDate;
          project.endDateString = this.formatter.formatDate(project.projectEndDate);
          project.projectTotalDays = response.projectTotalDays;
        }
      });
      this.loading = false;
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_PROJECT_SAVED'), true);
      this.updateTableFooterData();
      this.datasource._updateChangeSubscription();
    } catch (e) {
      this.cancelEdit(selectedProject);
      this.loading = false;
      this.snackBar.openSnackBar(this.translate.instant('ERROR_PROJECT_SAVED'), false);
    }
  }

  cancelEdit(selectedProject: any): void {
    let original = this.originalRowData[selectedProject.projectId];
    if (original) {
      selectedProject.projectName = original.projectName;
      selectedProject.projectPrice = original.projectPrice;
      selectedProject.projectEndDate = original.projectEndDate;
    }
    selectedProject['isEditing'] = false;
    this.isEditingRow = false;
  }


  handlePageEvent($event: PageEvent) {
    this.updateTableFooterData();
  }

  private updateTableFooterData(searching: boolean = false) {
    let displayedData: Project[]
    if (searching) {
      displayedData = this.datasource.filteredData;
    } else {
      displayedData = this.getDisplayedData();
    }
    this.getTotalDayRate(displayedData);
    this.getTotalPrice(displayedData);
    this.getTotalDays(displayedData);
  }

  private getTotalDayRate(displayedData: Project[]) {
    this.totalDayRate = displayedData.reduce((acc: number, project: Project) => acc + project.projectDayRate!, 0);
  }

  private getTotalPrice(displayedData: Project[]) {
    this.totalPrice = displayedData.reduce((acc: number, project: Project) => acc + project.projectPrice!, 0);
  }

  private getTotalDays(displayedData: Project[]) {
    this.totalDays = displayedData.reduce((acc: number, project: Project) => acc + project.projectTotalDays!, 0);
  }
  private getDisplayedData() {
    const startIndex = this.datasource.paginator!.pageIndex * this.datasource.paginator!.pageSize;
    const endIndex = startIndex + this.datasource.paginator!.pageSize;
    return this.datasource.data.slice(startIndex, endIndex);
  }
}
