import {AfterViewInit, Component, inject, ViewChild} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatIconModule} from '@angular/material/icon';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {NgClass, NgIf} from '@angular/common';
import {MatMenuItem, MatMenuModule, MatMenuTrigger} from '@angular/material/menu';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {AddProfileDialogComponent} from '../../modals/add-profile-dialog/add-profile-dialog.component';
import {FormsModule} from '@angular/forms';
import {MatFormField, MatInput} from '@angular/material/input';
import {MatLabel} from '@angular/material/form-field';
import {ProfileService} from '../../services/profile.service';
import {Router} from '@angular/router';
import {Profile} from '../../models';
import {SnackbarService} from '../../services/snackbar.service';
import {GeographyService} from '../../services/geography.service';

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
    FormsModule,
    MatInput,
    MatFormField,
    MatLabel,
    NgClass
  ],
  templateUrl: './profiles-page.component.html',
  styleUrl: './profiles-page.component.css'
})
export class ProfilesPageComponent implements AfterViewInit {

  constructor(private profileService: ProfileService,
              private router: Router,
              private snackBar: SnackbarService,
              private translate: TranslateService) {
  }

  //#region vars
  readonly dialog = inject(MatDialog);

  displayedColumns: string[] = [
    'name',
    'annual hours',
    'effective work hours',
    'effectiveness',
    'contributed annual cost',
    'allocated hours',
    'cost allocation',
    'location',
    'options'
  ];

  selectedRow: Profile | null = null;
  rowColor: Profile | null = null;

  datasource: MatTableDataSource<Profile> = new MatTableDataSource<Profile>();
  loading = true;
  originalRowData: { [key: number]: any } = {};
  isEditingRow: boolean = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  //#endregion

  async ngAfterViewInit() {
    let profiles = await this.profileService.getProfiles();
    for (let profile of profiles) {
      profile.location = profile.geography.name;
    }
    this.datasource.data = profiles;
    this.loading = false;
    this.datasource.sort = this.sort;
    this.datasource.paginator = this.paginator;
  }

  //#region functions
  editRow(element: any): void {
    if (this.isEditingRow) return;
    this.isEditingRow = true;
    element['isEditing'] = true;
    if (!this.originalRowData[element.id]) {
      this.originalRowData[element.id] = {...element}
    }
  }

  saveEdit(element: any): void {
    element['isEditing'] = false;
    this.isEditingRow = false;
    delete this.originalRowData[element.id];
    //todo: update call on api
    //remember to add snackbar
  }

  cancelEdit(element: any): void {
    let original = this.originalRowData[element.id];
    if (original) {
      element.name = original.name;
      element.annualHours = original.annualHours;
      element.effectiveWorkHours = original.effectiveWorkHours;
      element.effectivenessPercentage = original.effectivenessPercentage;
      element.annualCost = original.annualCost;
      element.totalHourAllocation = original.totalHourAllocation;
      element.totalCostAllocation = original.totalCostAllocation;
      element.geography.name = original.geography.name;
    }
    element['isEditing'] = false;
    this.isEditingRow = false;
  }

  goToProfile(profileId: string): void {
    this.router.navigate(['/profile', profileId]);
  }

  openDialog() {
    const dialogRef = this.dialog.open(AddProfileDialogComponent);

    dialogRef.componentInstance.profileAdded.subscribe((profile: Profile) => {
      this.datasource.data.push(profile)
      this.datasource._updateChangeSubscription();
    });
  }

  async onDelete() {
    const result = await this.profileService.deleteProfile(this.selectedRow?.profileId!)
    if (result) {
      this.datasource.data = this.datasource.data.filter((profile: Profile) => profile.profileId !== this.selectedRow?.profileId);
      this.datasource._updateChangeSubscription();
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_PROFILE_DELETED'), true);
    } else {
      this.snackBar.openSnackBar(this.translate.instant('ERROR_PROFILE_DELETED'), false);
    }
  }

  selectRow(row: Profile) {
    this.selectedRow = row;
  }

  allocationColor(row: Profile): string {
    const totalHourAllocation = row.totalHourAllocation ?? 0;
    const totalCostAllocation = row.totalCostAllocation ?? 0;

    this.rowColor = row;
    if (totalHourAllocation > 100 || totalCostAllocation > 100) {
      return 'red';
    } else if (totalHourAllocation <= 99 || totalCostAllocation <= 99) {
      return 'orange';
    }
    return '';
  }

  //#endregion
}
