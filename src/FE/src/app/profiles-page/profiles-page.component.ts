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
import {FormsModule} from '@angular/forms';
import {MatFormField, MatInput} from '@angular/material/input';
import {MatLabel} from '@angular/material/form-field';
import {ProfileService} from '../services/profile.service';
import {Router} from '@angular/router';

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
    MatLabel
  ],
  templateUrl: './profiles-page.component.html',
  styleUrl: './profiles-page.component.css'
})
export class ProfilesPageComponent implements AfterViewInit {
  readonly dialog = inject(MatDialog);

  openDialog() {
    const dialogRef = this.dialog.open(AddProfileDialogComponent);
  }

  constructor(private profileService: ProfileService, private  router: Router) {
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
  originalRowData: { [key: number]: any } = {};
  isEditingRow: boolean = false;
  selectedRow: any;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  async ngAfterViewInit() {
    let profiles = await this.profileService.getProfiles();
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
  }

  cancelEdit(element: any):void {
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

  selectRow(row: any): void {
    this.selectedRow = row;
  }

  //#endregion
}
