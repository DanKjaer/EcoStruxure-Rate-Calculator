import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatFormField, MatLabel, MatPrefix} from '@angular/material/form-field';
import {MatIcon} from '@angular/material/icon';
import {MatInput} from '@angular/material/input';
import {MatMenu, MatMenuItem} from '@angular/material/menu';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {DecimalPipe, NgClass, NgIf} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {Team, TeamDTO, TeamProfile} from '../../models';
import {ActivatedRoute} from '@angular/router';
import {MenuService} from '../../services/menu.service';
import {CurrencyService} from '../../services/currency.service';
import {TeamsService} from '../../services/teams.service';
import {AddToTeamDialogComponent} from '../../modals/add-to-team-dialog/add-to-team-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {SnackbarService} from '../../services/snackbar.service';
import {CalculationsService} from '../../services/calculations.service';
import {GenerateDTOService} from '../../services/generate-dto.service';

@Component({
  selector: 'app-team-page',
  standalone: true,
  imports: [
    MatButton,
    MatTableModule,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatMenu,
    MatMenuItem,
    MatPrefix,
    MatProgressSpinner,
    NgIf,
    ReactiveFormsModule,
    TranslateModule,
    NgClass,
    DecimalPipe,
    FormsModule
  ],
  templateUrl: './team-page.component.html',
  styleUrl: './team-page.component.css'
})
export class TeamPageComponent implements OnInit {
  readonly dialog = inject(MatDialog);
  team!: Team;
  teamProfiles: TeamProfile[] = [];
  isMenuOpen: boolean | undefined;
  originalRowData: { [key: number]: any } = {};
  protected isEditingRow: boolean = false;

  statBoxes = {
    rawHourlyRate: 0,
    markupHourlyRate: 0,
    gmHourlyRate: 0,
    totalAnnualHours: 0
  };
  protected readonly localStorage = localStorage;
  loading: boolean = true;
  datasource: MatTableDataSource<any> = new MatTableDataSource();
  displayedColumns: string[] = [
    'name',
    'hours allocation',
    'annual hours allocated',
    'cost allocation',
    'cost on team',
    'location',
    'day rate',
    'remove'
  ];

  constructor(private teamService: TeamsService,
              private menuService: MenuService,
              protected currencyService: CurrencyService,
              private snackBar: SnackbarService,
              private translate: TranslateService,
              protected calculationsService: CalculationsService,
              private generateDTOService: GenerateDTOService,
              private route: ActivatedRoute,
              private changeDetectorRef: ChangeDetectorRef) {}



  async ngOnInit() {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    });
    this.team = await this.teamService.getTeam(this.route.snapshot.paramMap.get('id')!);
    this.teamProfiles = this.team.teamProfiles!;
    this.datasource.data = this.teamProfiles;
    this.fillStatBoxes();
    this.loading = false;
  }

  private fillStatBoxes() {
    let markupHourlyRate = this.team.hourlyRate! * ((this.team.markupPercentage! / 100) + 1);
    let gmHourlyRate = markupHourlyRate * ((this.team.grossMarginPercentage! / 100) + 1);
    this.statBoxes = {
      rawHourlyRate: this.team.hourlyRate!,
      markupHourlyRate: markupHourlyRate,
      gmHourlyRate: gmHourlyRate,
      totalAnnualHours: this.teamProfiles.reduce((sum, item) => sum + item.allocatedHours!, 0)
    }
  }

  openDialog() {
    const dialogRef = this.dialog.open(AddToTeamDialogComponent, {
      minHeight: '60vh',
      maxHeight: '800px',
      minWidth: '50vw',
      maxWidth: '1000px',
    });
    this.loading = true;
    dialogRef.componentInstance.team = this.team;
    dialogRef.componentInstance.AddToTeam.subscribe((team: Team) => {
      this.team = team;
      this.datasource.data = this.team.teamProfiles!;
      this.fillStatBoxes();
    });
    this.loading = false;
    this.datasource._updateChangeSubscription();
    this.changeDetectorRef.detectChanges();
  }

  applySearch(event: Event) {
    const searchValue = (event.target as HTMLInputElement).value;
    this.datasource.filter = searchValue.trim().toLowerCase();

    if (this.datasource.paginator) {
      this.datasource.paginator.firstPage();
    }
  }

  async onRemove(row: TeamProfile) {
    if (!row || !row.profile?.profileId) {
      this.snackBar.openSnackBar(this.translate.instant('ERROR_PROFILE_DELETED'), false);
      return;
    }

    const result = await this.teamService.deleteTeamProfile(row.teamProfileId!, this.team.teamId!);
    if (result) {
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_PROFILE_DELETED'), true);
      this.team = await this.teamService.getTeam(this.route.snapshot.paramMap.get('id')!);
      this.teamProfiles = this.team.teamProfiles!;
      this.datasource.data = this.teamProfiles;
      this.datasource._updateChangeSubscription();
      this.fillStatBoxes();
    } else {
      this.snackBar.openSnackBar(this.translate.instant('ERROR_PROFILE_DELETED'), false);
    }
  }

  editRow(selectedTeamProfile: any) {
    if (this.isEditingRow) {
      return;
    }
    this.isEditingRow = true;
    selectedTeamProfile['isEditing'] = true;
    if (!this.originalRowData[selectedTeamProfile.teamProfileId]){
      this.originalRowData[selectedTeamProfile.teamProfileId] = {...selectedTeamProfile};
    }
  }

  async saveEdit(selectedTeamProfile: any) {
    this.loading = true;
    //apply change to the team profile
    selectedTeamProfile['isEditing'] = false;
    this.isEditingRow = false;
    selectedTeamProfile.allocatedCost = this.calculationsService.calculateCostAllocation(selectedTeamProfile);
    selectedTeamProfile.allocatedHours = this.calculationsService.calculateHourAllocation(selectedTeamProfile);
    this.teamProfiles.forEach((teamProfile) => {
      if (teamProfile.teamProfileId === selectedTeamProfile.teamProfileId) {
        teamProfile.allocationPercentageCost = selectedTeamProfile.allocationPercentageCost;
        teamProfile.allocationPercentageHours = selectedTeamProfile.allocationPercentageHours;
        teamProfile.allocatedCost = selectedTeamProfile.allocatedCost;
        teamProfile.allocatedHours = selectedTeamProfile.allocatedHours;
      }
    });
    this.team.teamProfiles = this.teamProfiles;

    //update team
    let DTO: TeamDTO = this.generateDTOService.createTeamDTO(this.team);
    let updatedTeam = await this.teamService.putTeam(DTO);
    if (updatedTeam) {
      this.team = updatedTeam;
      this.teamProfiles = this.team.teamProfiles!;
      this.datasource.data = this.teamProfiles!;
      this.fillStatBoxes();
      this.loading = false;
      this.snackBar.openSnackBar(this.translate.instant('SUCCESS_TEAM_UPDATED'), true);
    } else {
      this.loading = false;
      this.snackBar.openSnackBar(this.translate.instant('ERROR_TEAM_UPDATED'), false);
    }
  }

  cancelEdit(selectedTeamProfile: any) {
    let original = this.originalRowData[selectedTeamProfile.teamProfileId!];
    if (original) {
      selectedTeamProfile.allocationPercentageCost = original.allocationPercentageCost;
      selectedTeamProfile.allocationPercentageHours = original.allocationPercentageHours;
    }
    selectedTeamProfile['isEditing'] = false;
    this.isEditingRow = false;
  }
}
