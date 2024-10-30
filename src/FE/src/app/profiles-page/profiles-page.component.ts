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
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Profile} from '../models';
import {Router, RouterLink} from '@angular/router';

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
    MatDialogModule,
    RouterLink
  ],
  templateUrl: './profiles-page.component.html',
  styleUrl: './profiles-page.component.css'
})
export class ProfilesPageComponent implements AfterViewInit {
  constructor(private http: HttpClient, private router: Router) {
  }

  //#region vars
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

  selectedRow: any;

  datasource = new MatTableDataSource([{}]);
  loading = true;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  //#endregion

  //#region Dialog

  readonly dialog = inject(MatDialog);
  openDialog() {
    const dialogRef = this.dialog.open(AddProfileDialogComponent);
  }

  //#endregion

  //#region API

  private apiUrl = 'http://localhost:8080/api';

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

  //#endregion

  goToProfile(profileId: string): void {
    this.router.navigate(['/profile', profileId]);
  }

  selectRow(row: any): void {
    this.selectedRow = row;
  }

  ngAfterViewInit() {
    this.getProfiles().subscribe(profiles => {
      console.log(profiles);
      this.datasource.data = profiles;
      this.loading = false;
    })
    this.datasource.sort = this.sort;
    this.datasource.paginator = this.paginator;
  }
}
