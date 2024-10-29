import {AfterViewInit, Component, ViewChild} from '@angular/core';
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
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Profile} from '../models';

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
  ],
  templateUrl: './profiles-page.component.html',
  styleUrl: './profiles-page.component.css'
})
export class ProfilesPageComponent implements AfterViewInit {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {
  }

  getProfiles(): Observable<Profile[]> {
    return this.http.get<Profile[]>(`${this.apiUrl}/profile`);
  }

  postProfile(): Observable<Profile> {
    return this.http.post<Profile>(`${this.apiUrl}/profile`, {});
  }

  putProfile(): Observable<boolean> {
    return this.http.put<boolean>(`${this.apiUrl}/profile/{id}`, {});
  }

  deleteProfile(): Observable<boolean> {
    return this.http.delete<boolean>(`${this.apiUrl}/profile/{id}`);
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
  ]

  datasource = new MatTableDataSource([{}]);
  loading = true;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  ngAfterViewInit() {
    this.getProfiles().subscribe(profiles => {
      console.log(profiles);
      this.datasource.data = profiles;
      this.loading = false;
    })
    this.datasource.sort = this.sort;
    this.datasource.paginator = this.paginator;
/*    setTimeout(() => {
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
    }, 2000);*/
  }
}
