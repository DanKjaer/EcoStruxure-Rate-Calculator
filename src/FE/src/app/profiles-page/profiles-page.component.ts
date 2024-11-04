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
import {ProfileService} from '../services/profile.service';
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
    MatDialogModule
  ],
  templateUrl: './profiles-page.component.html',
  styleUrl: './profiles-page.component.css'
})
export class ProfilesPageComponent implements AfterViewInit {
  readonly dialog = inject(MatDialog);

  openDialog() {
    const dialogRef = this.dialog.open(AddProfileDialogComponent);

    dialogRef.componentInstance.profileAdded.subscribe((profile: Profile) => {
      this.datasource.data.push(profile)
      this.datasource._updateChangeSubscription();
    });
  }


  constructor(private profileService: ProfileService) {
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

  selectedRow: Profile | null = null;

  datasource: MatTableDataSource<Profile> = new MatTableDataSource<Profile>();
  loading = true;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  async ngAfterViewInit() {
    let profiles = await this.profileService.getProfiles();
    this.datasource.data = profiles;
    this.loading = false;
    this.datasource.sort = this.sort;
    this.datasource.paginator = this.paginator;
  }

  async onDelete() {
    const result = await this.profileService.deleteProfile(this.selectedRow?.profileId!)
    if (result) {
      this.datasource.data = this.datasource.data.filter((profile: Profile) => profile.profileId !== this.selectedRow?.profileId);
      this.datasource._updateChangeSubscription();
    }
  }


  selectRow(row: Profile) {
    this.selectedRow = row;
  }
}
