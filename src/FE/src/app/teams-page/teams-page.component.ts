import {AfterViewInit, Component, inject, ViewChild} from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {MatIcon} from '@angular/material/icon';
import {MatButton, MatIconButton} from '@angular/material/button';
import {NgIf} from '@angular/common';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {AddTeamsDialogComponent} from '../add-teams-dialog/add-teams-dialog.component';
import {HttpClient} from '@angular/common/http';
import {Team} from '../models';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-teams-page',
  standalone: true,
  imports: [
    TranslateModule,
    MatPaginatorModule,
    MatTableModule,
    MatSortModule,
    MatIcon,
    MatIconButton,
    NgIf,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger,
    MatProgressSpinner,
    MatButton,
    MatDialogModule
  ],
  templateUrl: './teams-page.component.html',
  styleUrl: './teams-page.component.css'
})
export class TeamsPageComponent implements AfterViewInit {
  readonly dialog = inject(MatDialog);

  openDialog() {
    const dialogRef = this.dialog.open(AddTeamsDialogComponent);
  }


  constructor(private http: HttpClient) {
  }



  displayedColumns: string[] = [
    'name',
    'markup',
    'gm',
    'updated',
    'hourly rate',
    'day rate',
    'total annual cost',
    'total annual hours',
    'options'
  ]

  datasource = new MatTableDataSource([{}]);
  loading = true;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  ngAfterViewInit() {
    this.getTeams().subscribe(teams => {
      this.datasource.data = teams;
      this.loading = false
    })
    this.datasource.sort = this.sort;
    this.datasource.paginator = this.paginator;
  }
}
