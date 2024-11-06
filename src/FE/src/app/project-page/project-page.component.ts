import {AfterViewInit, Component, inject, OnInit, ViewChild} from '@angular/core';
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
import {NgIf} from '@angular/common';
import {TranslateModule} from '@ngx-translate/core';
import {MatDialog} from '@angular/material/dialog';
import {Project, Team} from '../models';
import {ProjectService} from '../services/project.service';
import {FormatterService} from '../services/formatter.service';

@Component({
  selector: 'app-project-page',
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
    MatHeaderCellDef
  ],
  templateUrl: './project-page.component.html',
  styleUrl: './project-page.component.css'
})
export class ProjectPageComponent implements AfterViewInit, OnInit {
  readonly dialog = inject(MatDialog);

  datasource: MatTableDataSource<Project> = new MatTableDataSource<Project>();
  loading = true;
  displayedColumns: string[] = ['name', 'members', 'cost', 'margin', 'price', 'startDate', 'endDate', 'options'];
  selectedRow: Project | null = null;


  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private projectService: ProjectService, private formatter: FormatterService) { }

  async ngOnInit(): Promise<void> {
    this.loading = true;
    try {
      const projects = await this.projectService.getProjects();
      projects.forEach(project => {
        project.startDateString = this.formatter.formatDate(project.startDate);
        project.endDateString = this.formatter.formatDate(project.endDate);
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

  }

  onDelete() {

  }

  selectRow(row: Project) {

  }
}
