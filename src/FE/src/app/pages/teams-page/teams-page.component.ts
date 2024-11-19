import {AfterViewInit, Component, inject, OnInit, ViewChild} from '@angular/core';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {MatPaginator, MatPaginatorModule, PageEvent} from '@angular/material/paginator';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {MatIcon} from '@angular/material/icon';
import {MatButton, MatIconButton} from '@angular/material/button';
import {DecimalPipe, NgClass, NgIf} from '@angular/common';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {TeamsService} from '../../services/teams.service';
import {Team} from '../../models';
import {MatFormField, MatInput, MatLabel, MatPrefix} from '@angular/material/input';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {FormatterService} from '../../services/formatter.service';
import {AddTeamDialogComponent} from '../../modals/add-team-dialog/add-team-dialog.component';
import {Router} from '@angular/router';
import {SnackbarService} from '../../services/snackbar.service';
import {MenuService} from '../../services/menu.service';
import {CurrencyService} from '../../services/currency.service';

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
    MatDialogModule,
    MatInput,
    ReactiveFormsModule,
    FormsModule,
    NgClass,
    DecimalPipe,
    MatFormField,
    MatLabel,
    MatPrefix
  ],
  templateUrl: './teams-page.component.html',
  styleUrl: './teams-page.component.css'
})
export class TeamsPageComponent implements AfterViewInit, OnInit {
  readonly dialog = inject(MatDialog);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  datasource: MatTableDataSource<Team> = new MatTableDataSource<Team>();
  loading = true;
  displayedColumns: string[] = [
    'name',
    'markup',
    'gm',
    'updated',
    'hourly rate',
    'day rate',
    'total annual cost',
    'total annual hours',
    'total markup',
    'total gm',
    'options'
  ]
  selectedRow: Team | null = null;
  originalRowData: { [key: number]: any } = {};
  isEditingRow: boolean = false;

  protected readonly localStorage = localStorage;

  isMenuOpen: boolean | undefined;
  totalHourlyRate: number = 0;
  totalDayRate: number = 0;
  totalCost: number = 0;
  totalHours: number = 0;
  totalMarkup: number = 0;
  totalGrossMargin: number = 0;

  constructor(private teamService: TeamsService,
              private formatter: FormatterService,
              private router: Router,
              private snackBar: SnackbarService,
              private translate: TranslateService,
              private menuService: MenuService,
              protected currencyService: CurrencyService) {
  }

  ngOnInit() {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    });
  }

  async ngAfterViewInit() {
    let teams = await this.teamService.getTeams();
    teams.forEach(team => {
      team.updatedAtString = this.formatter.formatDateTime(team.updatedAt!);
    });
    this.datasource.data = teams;
    this.loading = false;
    this.datasource.sort = this.sort;
    this.datasource.paginator = this.paginator;
    this.updateTableFooterData();
  }

  applySearch(event: Event) {
    const searchValue = (event.target as HTMLInputElement).value;
    this.datasource.filter = searchValue.trim().toLowerCase();

    if (this.datasource.paginator) {
      this.datasource.paginator.firstPage();
    }
    this.updateTableFooterData(true);
  }

  openDialog() {
    this.loading = true;
    const dialogRef = this.dialog.open(AddTeamDialogComponent);

    dialogRef.componentInstance.teamAdded.subscribe((team: Team) => {
      team.updatedAtString = this.formatter.formatDateTime(team.updatedAt!);
      this.datasource.data.push(team);
      this.datasource._updateChangeSubscription();
    });
    this.updateTableFooterData();
    this.loading = false;
  }

  async onDelete() {
    const result = await this.teamService.deleteTeam(this.selectedRow?.teamId!)
    this.loading = true;
    if (result) {
      this.datasource.data = this.datasource.data.filter((team: Team) => team.teamId !== this.selectedRow?.teamId);
      this.datasource._updateChangeSubscription();
      this.updateTableFooterData();
      this.loading = false;
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_TEAM_DELETE'), true);
    } else {
      this.loading = false;
      this.snackBar.openSnackBar(this.translate.instant('ERROR_TEAM_DELETE'), true);
    }
  }

  editRow(element: any): void {
    if (this.isEditingRow) return;
    this.isEditingRow = true;
    element['isEditing'] = true;
    if (!this.originalRowData[element.id]) {
      this.originalRowData[element.id] = {...element}
    }
  }

  async saveEdit(selectedTeam: any): Promise<void> {
    selectedTeam['isEditing'] = false;
    this.isEditingRow = false;
    this.loading = true;

    let result : Team | null = null;
    try{
      result = await this.teamService.putTeam(selectedTeam);
      this.updateTableFooterData();
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_TEAM_SAVED'), true);
      this.loading = false;
    } catch (e) {
      this.cancelEdit(selectedTeam);
      this.snackBar.openSnackBar(this.translate.instant('ERROR_TEAM_SAVED'), false);
      this.loading = false;
      return;
    }

    const index = this.datasource.data.findIndex((team: Team) => team.teamId === selectedTeam.teamId);
    if (index !== -1) {
      this.datasource.data[index] = result;
      this.datasource._updateChangeSubscription();
      delete this.originalRowData[selectedTeam!.teamId!];
    }
  }

  cancelEdit(selectedTeam: any): void {
    let original = this.originalRowData[selectedTeam.id];
    if (original) {
      selectedTeam.name = original.name;
      selectedTeam.markup = original.markup;
      selectedTeam.grossMargin = original.grossMargin;
      selectedTeam.updatedAt = original.updatedAt;
    }
    selectedTeam['isEditing'] = false;
    this.isEditingRow = false;
  }

  selectRow(row: Team) {
    this.selectedRow = row;
  }

  goToTeam(teamId: string) {
    this.router.navigate(['/team', teamId]);
  }


  handlePageEvent($event: PageEvent) {
    this.updateTableFooterData();
  }

  private updateTableFooterData(searching: boolean = false) {
    let displayedData: Team[];
    if (searching) {
      displayedData = this.datasource.filteredData;
    } else {
      displayedData = this.getDisplayedData();
    }
    this.getTotalHourlyRate(displayedData);
    this.getTotalDayRate(displayedData);
    this.getTotalCost(displayedData);
    this.getTotalHours(displayedData);
    this.gettotalMarkup(displayedData);
    this.getTotalGrossMargin(displayedData);
  }

  private getTotalHourlyRate(displayedData: Team[]) {
    this.totalHourlyRate = displayedData.reduce((acc: number, team: Team) => acc + team.hourlyRate!, 0);
  }

  private getTotalDayRate(displayedData: Team[]) {
    this.totalDayRate = displayedData.reduce((acc: number, team: Team) => acc + team.dayRate!, 0);
  }

  private getTotalCost(displayedData: Team[]) {
    this.totalCost = displayedData.reduce((acc: number, team: Team) => acc + team.totalAllocatedCost!, 0);
  }

  private getTotalHours(displayedData: Team[]) {
    this.totalHours = displayedData.reduce((acc: number, team: Team) => acc + team.totalAllocatedHours!, 0);
  }

  private gettotalMarkup(displayedData: Team[]) {
    this.totalMarkup = displayedData.reduce((acc: number, team: Team) => acc + team.totalMarkup!, 0);
  }

  private getTotalGrossMargin(displayedData: Team[]) {
    this.totalGrossMargin = displayedData.reduce((acc: number, team: Team) => acc + team.totalGrossMargin!, 0);
  }

  private getDisplayedData() {
    const startIndex = this.datasource.paginator!.pageIndex * this.datasource.paginator!.pageSize;
    const endIndex = startIndex + this.datasource.paginator!.pageSize;
    return this.datasource.data.slice(startIndex, endIndex);
  }
}
