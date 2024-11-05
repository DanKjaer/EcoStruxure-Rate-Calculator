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
import {Team} from '../models';
import {TeamsService} from '../services/teams.service';

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

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private teamService: TeamsService) { }

  openDialog() {
    const dialogRef = this.dialog.open(AddTeamsDialogComponent);

    dialogRef.componentInstance.teamAdded.subscribe((team: Team) => {
      this.datasource.data.push(team);
      this.datasource._updateChangeSubscription();
    });
  }
  async ngAfterViewInit() {
    let teams = await this.teamService.getTeams();
    this.datasource.data = teams;
    this.loading = false;
    this.datasource.sort = this.sort;
    this.datasource.paginator = this.paginator;
  }

  async onDelete() {
    // Add toast to show success or failure
    const result = await this.teamService.deleteTeam(this.selectedRow?.teamId!)
    if (result) {
      this.datasource.data = this.datasource.data.filter((team: Team) => team.teamId !== this.selectedRow?.teamId);
      this.datasource._updateChangeSubscription();
    }
  }

  selectRow(row: Team) {
    this.selectedRow = row;
  }
}
