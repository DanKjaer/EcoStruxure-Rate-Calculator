import {ChangeDetectorRef, Component, computed, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatFormField, MatLabel, MatPrefix, MatSuffix} from '@angular/material/form-field';
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
import {SearchConfigService} from '../../services/search-config.service';

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
    FormsModule,
    MatSuffix
  ],
  templateUrl: './team-page.component.html',
  styleUrl: './team-page.component.css'
})
export class TeamPageComponent implements OnInit {
  readonly dialog = inject(MatDialog);
  team: WritableSignal<Team | null> = signal<Team | null>(null);
  teamProfiles: WritableSignal<TeamProfile[]> = signal<TeamProfile[]>([]);
  isMenuOpen: boolean | undefined;
  originalRowData: { [key: number]: any } = {};
  protected isEditingRow: boolean = false;
  value: string = '';

  statBoxes = computed(() => {
    const team = this.team();
    const teamProfiles = this.teamProfiles();
    let rawHourlyRate: number = 0;
    let markupHourlyRate: number = 0;
    let gmHourlyRate: number = 0;
    let totalAnnualHours: number = 0;

    if (!team) {
      return {rawHourlyRate, markupHourlyRate, gmHourlyRate, totalAnnualHours};
    }
    rawHourlyRate = team.hourlyRate!;

    if (team.markupPercentage === 0) {
      markupHourlyRate = team.hourlyRate!;
    } else {
      markupHourlyRate = team.hourlyRate! * ((team.markupPercentage! / 100) + 1);
    }
    if (team.grossMarginPercentage === 0) {
      gmHourlyRate = markupHourlyRate;
    } else {
      gmHourlyRate = markupHourlyRate * ((team.grossMarginPercentage! / 100) + 1);
    }
    totalAnnualHours = teamProfiles.reduce((sum, item) =>
      sum + item.allocatedHours!, 0);

    return {rawHourlyRate, markupHourlyRate, gmHourlyRate, totalAnnualHours};
  });
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
              private snackbarService: SnackbarService,
              private translateService: TranslateService,
              protected calculationsService: CalculationsService,
              private searchConfigService: SearchConfigService,
              private generateDTOService: GenerateDTOService,
              private route: ActivatedRoute,
              private changeDetectorRef: ChangeDetectorRef) {
  }


  async ngOnInit() {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    });
    this.team.set(await this.teamService.getTeam(this.route.snapshot.paramMap.get('id')!));
    this.teamProfiles.set(this.team()!.teamProfiles!);
    this.datasource.data = this.teamProfiles();
    this.searchConfigService.configureFilter(this.datasource, ['profile.name', 'profile.geography.name']);
    this.loading = false;
  }


  openDialog() {
    const dialogRef = this.dialog.open(AddToTeamDialogComponent, {
      minHeight: '60vh',
      maxHeight: '800px',
      minWidth: '50vw',
      maxWidth: '1000px',
    });
    this.loading = true;
    dialogRef.componentInstance.team = this.team()!;
    dialogRef.componentInstance.AddToTeam.subscribe((team: Team) => {
      this.team.set(team);
      this.datasource.data = this.team()!.teamProfiles!;
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

  clearSearch() {
    this.value = '';
    this.applySearch({target: {value: ''}} as unknown as Event);
  }

  async onRemove(row: TeamProfile) {
    if (!row || !row.profile?.profileId) {
      this.snackbarService.openSnackBar(this.translateService.instant('ERROR_PROFILE_DELETED'), false);
      return;
    }

    const result = await this.teamService.deleteTeamProfile(row.teamProfileId!, this.team()!.teamId!);
    if (result) {
      this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_PROFILE_DELETED'), true);
      this.team.set(await this.teamService.getTeam(this.route.snapshot.paramMap.get('id')!));
      this.teamProfiles.set(this.team()!.teamProfiles!);
      this.datasource.data = this.teamProfiles();
      this.datasource._updateChangeSubscription();
    } else {
      this.snackbarService.openSnackBar(this.translateService.instant('ERROR_PROFILE_DELETED'), false);
    }
  }

  editRow(selectedTeamProfile: any) {
    if (this.isEditingRow) {
      return;
    }
    this.isEditingRow = true;
    selectedTeamProfile['isEditing'] = true;
    if (!this.originalRowData[selectedTeamProfile.teamProfileId]) {
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
    this.teamProfiles().forEach((teamProfile) => {
      if (teamProfile.teamProfileId === selectedTeamProfile.teamProfileId) {
        teamProfile.allocationPercentageCost = selectedTeamProfile.allocationPercentageCost;
        teamProfile.allocationPercentageHours = selectedTeamProfile.allocationPercentageHours;
        teamProfile.allocatedCost = selectedTeamProfile.allocatedCost;
        teamProfile.allocatedHours = selectedTeamProfile.allocatedHours;
      }
    });
    this.team()!.teamProfiles = this.teamProfiles();

    //update team
    let DTO: TeamDTO = this.generateDTOService.createTeamDTO(this.team);
    let updatedTeam = await this.teamService.putTeam(DTO);
    if (updatedTeam) {
      this.team.set(updatedTeam);
      this.teamProfiles.set(this.team()!.teamProfiles!);
      this.datasource.data = this.teamProfiles();
      this.loading = false;
      this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_TEAM_UPDATED'), true);
    } else {
      this.loading = false;
      this.snackbarService.openSnackBar(this.translateService.instant('ERROR_TEAM_UPDATED'), false);
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
