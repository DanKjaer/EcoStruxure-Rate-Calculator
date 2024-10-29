import {AfterViewInit, Component, inject, ViewChild} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {TranslateModule} from '@ngx-translate/core';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatIconModule} from '@angular/material/icon';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {NgIf} from '@angular/common';
import {MatMenuItem, MatMenuModule, MatMenuTrigger} from '@angular/material/menu';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {AddProfileDialogComponent} from '../add-profile-dialog/add-profile-dialog.component';

@Component({
  selector: 'app-profiles-page',
  standalone: true,
  imports: [
    MatButtonModule,
    MatTooltipModule,
    MatCardModule,
    MatIconModule,
    TranslateModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatProgressSpinnerModule,
    NgIf,
    MatMenuTrigger,
    MatMenuModule,
    MatMenuItem,
    MatDialogModule
  ],
  templateUrl: './profiles-page.component.html',
  styleUrl: './profiles-page.component.css'
})
export class ProfilesPageComponent implements AfterViewInit {
  readonly dialog = inject(MatDialog);

  openDialog() {
    const dialogRef = this.dialog.open(AddProfileDialogComponent);
  }

  displayedColumns: string[] = [
    'name',
    'annual hours',
    'effective work hours',
    'effectiveness',
    'contributed annual cost',
    'allocated hours',
    'cost allocation',
    'options'
  ];

  datasource = new MatTableDataSource([{}]);
  loading = true;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  ngAfterViewInit() {
    this.datasource.sort = this.sort;
    this.datasource.paginator = this.paginator;
    setTimeout(() => {
      this.datasource.data = [
        {
          name: 'John Doe',
          'annual hours': 2080,
          'effective work hours': 1664,
          effectiveness: 80,
          'contributed annual cost': 80000,
          'allocated hours': 240,
          'cost allocation': 240
        },
        {
          name: 'John Smith',
          'annual hours': 1080,
          'effective work hours': 864,
          effectiveness: 100,
          'contributed annual cost': 800000,
          'allocated hours': 120,
          'cost allocation': 120
        },
        {
          name: 'John Smith',
          'annual hours': 1080,
          'effective work hours': 864,
          effectiveness: 100,
          'contributed annual cost': 800000,
          'allocated hours': 120,
          'cost allocation': 120
        },
        {
          name: 'John Smith',
          'annual hours': 1080,
          'effective work hours': 864,
          effectiveness: 100,
          'contributed annual cost': 800000,
          'allocated hours': 120,
          'cost allocation': 120
        },
        {
          name: 'John Smith',
          'annual hours': 1080,
          'effective work hours': 864,
          effectiveness: 100,
          'contributed annual cost': 800000,
          'allocated hours': 120,
          'cost allocation': 120
        },
        {
          name: 'John Smith',
          'annual hours': 1080,
          'effective work hours': 864,
          effectiveness: 100,
          'contributed annual cost': 800000,
          'allocated hours': 120,
          'cost allocation': 120
        },
        {
          name: 'John Smith',
          'annual hours': 1080,
          'effective work hours': 864,
          effectiveness: 100,
          'contributed annual cost': 800000,
          'allocated hours': 120,
          'cost allocation': 120
        },
        {
          name: 'John Smith',
          'annual hours': 1080,
          'effective work hours': 864,
          effectiveness: 100,
          'contributed annual cost': 800000,
          'allocated hours': 120,
          'cost allocation': 120
        },
        {
          name: 'John Smith',
          'annual hours': 1080,
          'effective work hours': 864,
          effectiveness: 100,
          'contributed annual cost': 800000,
          'allocated hours': 120,
          'cost allocation': 120
        },
        {
          name: 'John Smith',
          'annual hours': 1080,
          'effective work hours': 864,
          effectiveness: 100,
          'contributed annual cost': 800000,
          'allocated hours': 120,
          'cost allocation': 120
        },
        {
          name: 'John Smith',
          'annual hours': 1080,
          'effective work hours': 864,
          effectiveness: 100,
          'contributed annual cost': 800000,
          'allocated hours': 120,
          'cost allocation': 120
        },
        {
          name: 'John Smith',
          'annual hours': 1080,
          'effective work hours': 864,
          effectiveness: 100,
          'contributed annual cost': 800000,
          'allocated hours': 120,
          'cost allocation': 120
        },
        {
          name: 'John Smith',
          'annual hours': 1080,
          'effective work hours': 864,
          effectiveness: 100,
          'contributed annual cost': 800000,
          'allocated hours': 120,
          'cost allocation': 120
        },
        {
          name: 'John Smith',
          'annual hours': 1080,
          'effective work hours': 864,
          effectiveness: 100,
          'contributed annual cost': 800000,
          'allocated hours': 120,
          'cost allocation': 120
        }
      ];
      this.loading = false;
    }, 2000);
  }
}
