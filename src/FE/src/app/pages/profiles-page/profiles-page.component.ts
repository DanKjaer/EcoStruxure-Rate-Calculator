import {AfterViewInit, Component, inject, OnInit, ViewChild} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatIconModule} from '@angular/material/icon';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatPaginator, MatPaginatorModule, PageEvent} from '@angular/material/paginator';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {DecimalPipe, NgClass, NgIf} from '@angular/common';
import {MatMenuItem, MatMenuModule, MatMenuTrigger} from '@angular/material/menu';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {AddProfileDialogComponent} from '../../modals/add-profile-dialog/add-profile-dialog.component';
import {FormsModule} from '@angular/forms';
import {MatFormField, MatInput, MatPrefix} from '@angular/material/input';
import {ProfileService} from '../../services/profile.service';
import {Router} from '@angular/router';
import {Profile} from '../../models';
import {SnackbarService} from '../../services/snackbar.service';
import {MenuService} from '../../services/menu.service';
import {CurrencyService} from '../../services/currency.service';
import {MatLabel} from '@angular/material/form-field';

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
    NgClass,
    DecimalPipe,
    MatFormField,
    MatLabel,
    MatPrefix
  ],
  templateUrl: './profiles-page.component.html',
  styleUrl: './profiles-page.component.css'
})
export class ProfilesPageComponent implements AfterViewInit, OnInit {

  constructor(private profileService: ProfileService,
              private router: Router,
              private snackBar: SnackbarService,
              private translate: TranslateService,
              private menuService: MenuService,
              protected currencyService: CurrencyService) {
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
    'options'
  ];

  selectedRow: Profile | null = null;
  rowColor: Profile | null = null;

  protected readonly localStorage = localStorage;

  datasource: MatTableDataSource<Profile> = new MatTableDataSource<Profile>();
  loading = true;
  originalRowData: { [key: number]: any } = {};
  isEditingRow: boolean = false;
  isMenuOpen: boolean | undefined;
  averageCostAllocation: number = 0;
  averageHourAllocation: number = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  //#endregion

  //#region inits
  ngOnInit() {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    });
  }

  async ngAfterViewInit() {
    const profiles = await this.profileService.getProfiles();
    console.log(profiles);
    this.datasource.data = profiles;
    this.loading = false;
    this.datasource.sort = this.sort;
    this.datasource.paginator = this.paginator;
    this.updateTableFooterData();
  }

  //#endregion

  //#region functions
  applySearch(event: Event) {
    const searchValue = (event.target as HTMLInputElement).value;
    this.datasource.filter = searchValue.trim().toLowerCase();

    if (this.datasource.paginator) {
      this.datasource.paginator.firstPage();
    }
    this.updateTableFooterData(true);
  }

  editRow(element: any): void {
    if (this.isEditingRow) return;
    this.isEditingRow = true;
    element['isEditing'] = true;
    if (!this.originalRowData[element.id]) {
      this.originalRowData[element.id] = {...element}
    }
  }

  async saveEdit(element: any): Promise<void> {
    this.loading = true;
    element['isEditing'] = false;
    this.isEditingRow = false;

    try {
      let response = await this.profileService.putProfile(element);
      this.datasource.data.forEach((profile: Profile) => {
        if (profile.profileId === response.profileId) {
          profile.name = response.name;
          profile.annualHours = response.annualHours;
          profile.effectivenessPercentage = response.effectivenessPercentage;
          profile.annualCost = response.annualCost;
        }
      });
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_PROFILE_UPDATED'), true);
      this.updateTableFooterData();
      this.loading = false;
    } catch (e) {
      this.cancelEdit(element)
      this.snackBar.openSnackBar(this.translate.instant('ERROR_PROFILE_UPDATED'), false);
      this.loading = false;
    }
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
    }
    element['isEditing'] = false;
    this.isEditingRow = false;
  }

  goToProfile(profileId: string): void {
    this.router.navigate(['/profile', profileId]);
  }

  openDialog() {
    this.loading = true;
    const dialogRef = this.dialog.open(AddProfileDialogComponent);

    dialogRef.componentInstance.profileAdded.subscribe((profile: Profile) => {
      profile.totalCostAllocation = 0;
      profile.totalHourAllocation = 0;
      this.datasource.data.push(profile)
      this.datasource._updateChangeSubscription();
    });
    this.loading = false;
    this.updateTableFooterData();
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

  handlePageEvent($event: PageEvent) {
    this.updateTableFooterData();
  }

  updateTableFooterData(searching: boolean = false) {
    let displayedData: Profile[];
    if (searching) {
      displayedData = this.datasource.filteredData;
    } else {
      displayedData = this.getDisplayedData();
    }
    this.getAverageHourAllocation(displayedData);
    this.getAverageCostAllocation(displayedData);
  }

  getAverageHourAllocation(displayedData: Profile[]) {
    this.averageHourAllocation = displayedData.reduce((acc, profile) => acc + (profile.totalHourAllocation ?? 0), 0) / displayedData.length;
  }

  getAverageCostAllocation(displayedData: Profile[]) {
    this.averageCostAllocation = displayedData.reduce((acc, profile) => acc + (profile.totalCostAllocation ?? 0), 0) / displayedData.length;
  }

  getDisplayedData() {
    const startIndex = this.datasource.paginator!.pageIndex * this.datasource.paginator!.pageSize;
    const endIndex = startIndex + this.datasource.paginator!.pageSize;
    return this.datasource.data.slice(startIndex, endIndex);
  }
}
