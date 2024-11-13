import {AfterViewInit, ChangeDetectionStrategy, Component, inject, OnInit, ViewChild} from '@angular/core';
import {MatButton, MatIconButton} from '@angular/material/button';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable, MatTableDataSource
} from '@angular/material/table';
import {MatIcon} from '@angular/material/icon';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatPaginator} from '@angular/material/paginator';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatSort, MatSortHeader} from '@angular/material/sort';
import {NgClass, NgIf} from '@angular/common';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {MatDialog} from '@angular/material/dialog';
import {Project} from '../../models';
import {ProjectService} from '../../services/project.service';
import {FormatterService} from '../../services/formatter.service';
import {AddProjectDialogComponent} from '../../modals/add-project-dialog/add-project-dialog.component';
import {SnackbarService} from '../../services/snackbar.service';
import { ChangeDetectorRef } from '@angular/core';
import {MenuService} from '../../services/menu.service';

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
    MatProgressSpinner,
    MatRow,
    MatRowDef,
    MatSort,
    MatSortHeader,
    MatTable,
    NgIf,
    TranslateModule,
    MatHeaderCellDef,
    NgClass
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

  selectedRow: Project | null = null;
  datasource: MatTableDataSource<Project> = new MatTableDataSource<Project>();
  loading = true;
  isMenuOpen: boolean | undefined;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private projectService: ProjectService,
              private formatter: FormatterService,
              private snackBar: SnackbarService,
              private ChangeDetectorRef: ChangeDetectorRef,
              private translate: TranslateService,
              private menuService: MenuService) {}

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
    }
  }

  async ngAfterViewInit(): Promise<void> {
    this.datasource.sort = this.sort;
    this.datasource.paginator = this.paginator;
  }


  openDialog() {
    const dialogRef = this.dialog.open(AddProjectDialogComponent, {
      minHeight: '80vh',
      maxHeight: '800px',
      minWidth: '60vw',
      maxWidth: '1200px',
    });

    dialogRef.componentInstance.projectAdded.subscribe((project: Project) => {
      project.startDateString = this.formatter.formatDate(project.projectStartDate);
      project.endDateString = this.formatter.formatDate(project.projectEndDate);
      this.datasource.data.push(project);
      this.datasource._updateChangeSubscription();
    });

    this.ChangeDetectorRef.detectChanges();
  }

  async onDelete() {
    const result = await this.projectService.deleteProject(this.selectedRow?.projectId!);
    if (result) {
      this.datasource.data = this.datasource.data.filter((project: Project) => project.projectId !== this.selectedRow?.projectId);
      this.datasource._updateChangeSubscription();
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_PROJECT_DELETED'), true);
    } else {
      this.snackBar.openSnackBar(this.translate.instant('ERROR_PROJECT_DELETED'), false);
    }
  }

  selectRow(row: Project) {
    this.selectedRow = row;
  }
}