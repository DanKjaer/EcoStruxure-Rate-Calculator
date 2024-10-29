import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {MatIcon} from '@angular/material/icon';
import {MatButton, MatIconButton} from '@angular/material/button';
import {NgIf} from '@angular/common';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {dateTimestampProvider} from 'rxjs/internal/scheduler/dateTimestampProvider';

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
    MatButton
  ],
  templateUrl: './teams-page.component.html',
  styleUrl: './teams-page.component.css'
})
export class TeamsPageComponent implements AfterViewInit {
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
    this.datasource.sort = this.sort;
    this.datasource.paginator = this.paginator;
    setTimeout(() => {
      this.datasource.data = [
        {
          name: 'team 1',
          markup: 20,
          gm: 35,
          updated: Date.UTC(2024, 10, 29),
          'hourly rate': 67.31,
          'day rate': 538.48,
          'total annual cost': 420000,
          'total annual hours': 6240
        },
        {
          name: 'team 2',
          markup: 20,
          gm: 35,
          updated: Date.UTC(2024, 10, 29),
          'hourly rate': 67.31,
          'day rate': 538.48,
          'total annual cost': 420000,
          'total annual hours': 6240
        },
        {
          name: 'team 3',
          markup: 20,
          gm: 35,
          updated: Date.UTC(2024, 10, 29),
          'hourly rate': 67.31,
          'day rate': 538.48,
          'total annual cost': 420000,
          'total annual hours': 6240
        },
      ];
      this.loading = false;
    }, 2000)
  }
}
