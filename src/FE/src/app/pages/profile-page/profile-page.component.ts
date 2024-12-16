import {
  Component,
  computed, inject,
  OnInit,
  signal,
  WritableSignal
} from '@angular/core';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatOption} from '@angular/material/core';
import {MatRadioModule} from '@angular/material/radio';
import {MatSelect} from '@angular/material/select';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {DecimalPipe, NgClass, NgIf} from '@angular/common';
import {MatMenuModule} from '@angular/material/menu';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {ProfileService} from '../../services/profile.service';
import {ActivatedRoute} from '@angular/router';
import {Geography, Profile, TeamProfile} from '../../models';
import {GeographyService} from "../../services/geography.service";
import {TeamsService} from "../../services/teams.service";
import {MenuService} from '../../services/menu.service';
import {CurrencyService} from '../../services/currency.service';
import {SnackbarService} from '../../services/snackbar.service';
import {
  AddProfileToTeamDialogComponent
} from '../../modals/add-profile-to-team-dialog/add-profile-to-team-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {SearchConfigService} from '../../services/search-config.service';
import {CalculationsService} from '../../services/calculations.service';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [
    TranslateModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatOption,
    MatRadioModule,
    MatSelect,
    ReactiveFormsModule,
    MatIconModule,
    MatTableModule,
    NgIf,
    MatMenuModule,
    MatProgressSpinner,
    NgClass,
    DecimalPipe,
    FormsModule
  ],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.css'
})
export class ProfilePageComponent implements OnInit {
  profileForm: FormGroup = new FormGroup({});
  protected readonly localStorage = localStorage;
  locations: Geography[] = [];
  currentProfile: WritableSignal<Profile | null> = signal<Profile | null>(null);
  teams: WritableSignal<TeamProfile[]> = signal<TeamProfile[]>([]);
  loading: boolean = true;
  isMenuOpen: boolean | undefined;
  datasource: MatTableDataSource<any> = new MatTableDataSource();
  displayedColumns: string[] = [
    'name',
    'hour allocation',
    'annual hours',
    'cost allocation',
    'hourly rate',
    'day rate',
    'annual cost',
    'remove'
  ];
  originalRowData: { [key: number]: any } = {};
  protected isEditingRow: boolean = false;


  //Create a computed property to calculate the stat boxes
  statBoxes = computed(() => {
    const teams = this.teams();
    const profile = this.currentProfile();

    if (!profile) {
      return {
        totalDayRate: 0,
        totalHourlyRate: 0,
        totalAnnualCost: 0,
        totalAnnualHours: 0
      };
    } else if (profile.annualHours === 0) {
      return {
        totalDayRate: 0,
        totalHourlyRate: 0,
        totalAnnualCost: teams.reduce((sum, item) => sum + item.allocatedCost!, 0),
        totalAnnualHours: 0
      };
    }
    const totalDayRate = teams.reduce((sum, item) => sum + this.calculateDayRate(item, profile), 0);
    const totalHourlyRate = totalDayRate / profile.hoursPerDay!;
    const totalAnnualCost = teams.reduce((sum, item) => sum + item.allocatedCost!, 0);
    const totalAnnualHours = teams.reduce((sum, item) => sum + item.allocatedHours!, 0);

    return {totalDayRate, totalHourlyRate, totalAnnualCost, totalAnnualHours};
  });
  readonly dialog = inject(MatDialog);

  constructor(protected currencyService: CurrencyService,
              private profileService: ProfileService,
              private geographyService: GeographyService,
              private teamsService: TeamsService,
              private menuService: MenuService,
              private snackbarService: SnackbarService,
              private translateService: TranslateService,
              private searchConfigService: SearchConfigService,
              private calculationsService: CalculationsService,
              private teamService: TeamsService,
              private fb: FormBuilder,
              private route: ActivatedRoute) {
  }

  async ngOnInit() {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {

      this.isMenuOpen = isOpen;
    });
    this.profileForm = this.fb.group({
      name: ['', Validators.required],
      location: ['', Validators.required],
      resource_type: [null, Validators.required],
      annual_cost: ['', Validators.required],
      annual_hours: [{value: '', disabled: true}],
      hours_per_day: ['', [Validators.required, Validators.min(0), Validators.max(24)]],
      effectiveness: ['', [Validators.required, Validators.min(0), Validators.max(100)]]
    });

    this.locations = await this.geographyService.getCountries();

    let id = this.route.snapshot.paramMap.get('id')!;

    this.currentProfile.set(await this.profileService.getProfile(id));
    this.prepProfileForm();
    await this.prepTeamList(id);
    this.searchConfigService.configureFilter(this.datasource, ['team.name']);
    this.loading = false;
  }

  applySearch(event: Event) {
    const searchValue = (event.target as HTMLInputElement).value;
    this.datasource.filter = searchValue.trim().toLowerCase();

    if (this.datasource.paginator) {
      this.datasource.paginator.firstPage();
    }
  }

  openDialog() {
    this.loading = true;
    const dialogRef = this.dialog.open(AddProfileToTeamDialogComponent, {
      minHeight: '60vh',
      maxHeight: '800px',
      minWidth: '50vw',
      maxWidth: '1000px',
    });
    dialogRef.componentInstance.profile = this.currentProfile()!;
    dialogRef.componentInstance.teamProfiles = this.datasource.data;
    dialogRef.componentInstance.addedProfileToTeam.subscribe((teamProfiles: TeamProfile[]) => {
      this.datasource.data.push(...teamProfiles);
      this.teams.set(this.teams().concat(teamProfiles));
      this.datasource._updateChangeSubscription();
    });

    this.loading = false;
  }

  async update() {
    this.loading = true;
    try {
      this.currentProfile()!.name = this.profileForm.value.name;
      this.currentProfile()!.geography = this.locations.find((location) => {
        return location.id === this.profileForm.value.location
      })!;
      this.currentProfile()!.resourceType = this.profileForm.value.resource_type;
      this.currentProfile()!.annualCost = this.profileForm.value.annual_cost;
      this.currentProfile()!.annualHours = this.profileForm.value.annual_hours;
      this.currentProfile()!.effectivenessPercentage = this.profileForm.value.effectiveness;
      this.currentProfile()!.hoursPerDay = this.profileForm.value.hours_per_day;
      let response = await this.profileService.putProfile(this.currentProfile()!);
      this.currentProfile.set(response);
      this.prepTeamList(this.currentProfile()!.profileId!);
      this.prepProfileForm();
      this.snackbarService.openSnackBar(this.translateService.instant('REASON_UPDATED_PROFILE'), true);
    } catch (e) {
      this.snackbarService.openSnackBar(this.translateService.instant('FAILED_UPDATED_PROFILE'), false);
    }
    this.loading = false;
  }

  undo() {
    this.profileForm = this.fb.group({
      name: [this.currentProfile()!.name],
      location: [this.currentProfile()!.geography.id],
      resource_type: [this.currentProfile()!.resourceType],
      annual_cost: [this.currentProfile()!.annualCost],
      annual_hours: [this.currentProfile()!.annualHours],
      effectiveness: [this.currentProfile()!.effectivenessPercentage],
      hours_per_day: [this.currentProfile()!.hoursPerDay]
    })

    if (!this.profileForm.get('resource_type')?.value) {
      this.profileForm.get('annual_hours')?.enable();
    } else {
      this.profileForm.get('annual_hours')?.disable();
    }

    this.profileForm.get('resource_type')?.valueChanges.subscribe((value: boolean) => {
      if (value) {
        this.profileForm.get('annual_hours')?.reset({value: 0, disabled: true});
      } else {
        this.profileForm.get('annual_hours')?.enable();
      }
    });
  }

  async remove(row: TeamProfile) {
    let result = await this.teamsService.deleteTeamProfile(row.teamProfileId!, row.team!.teamId!);
    if (result) {
      this.teams.set(this.teams().filter(teamProfile => teamProfile !== row));
      this.datasource.data = this.datasource.data.filter(teamProfile => teamProfile !== row);
      this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_PROFILE_TEAM_REMOVED'), true);
    } else {
      this.snackbarService.openSnackBar(this.translateService.instant('ERROR_PROFILE_TEAM_REMOVED'), false);
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

  async saveEditRow(selectedTeamProfile: any) {
    this.loading = true;
    selectedTeamProfile['isEditing'] = false;
    this.isEditingRow = false;
    let teamProfileToUpdate: TeamProfile = selectedTeamProfile;
    teamProfileToUpdate.profile = this.currentProfile()!;
    teamProfileToUpdate.allocatedCost = this.calculationsService.calculateCostAllocation(teamProfileToUpdate);
    teamProfileToUpdate.allocatedHours = this.calculationsService.calculateHourAllocation(teamProfileToUpdate);
    teamProfileToUpdate.team = {
      teamId: teamProfileToUpdate.team!.teamId,
      name: teamProfileToUpdate.team!.name
    }
    try {
      let updatedTeamProfile = await this.teamService.addProfileToTeams([teamProfileToUpdate]);
      this.teams.set(this.teams().map((teamProfile) => {
        if (teamProfile.teamProfileId === updatedTeamProfile[0].teamProfileId) {
          return updatedTeamProfile[0];
        }
        return teamProfile;
      }));
      this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_PROFILE_TEAM_UPDATED'), true);
      this.loading = false;
    } catch (e) {
      this.snackbarService.openSnackBar(this.translateService.instant('ERROR_PROFILE_TEAM_UPDATED'), false);
      this.cancelEditRow(selectedTeamProfile);
      this.loading = false;
    }
  }

  cancelEditRow(selectedTeamProfile: any) {
    let original = this.originalRowData[selectedTeamProfile.teamProfileId!];
    if (original) {
      selectedTeamProfile.allocationPercentageCost = original.allocationPercentageCost;
      selectedTeamProfile.allocationPercentageHours = original.allocationPercentageHours;
    }
    selectedTeamProfile['isEditing'] = false;
    this.isEditingRow = false;
  }

  private async prepTeamList(id: string) {
    let response = await this.teamsService.getTeamProfiles(id);
    this.teams.set(response);
    this.datasource.data = response;
  }

  private prepProfileForm() {
    this.profileForm = this.fb.group({
      name: [this.currentProfile()!.name, Validators.required],
      location: [this.currentProfile()!.geography.id, Validators.required],
      resource_type: [this.currentProfile()!.resourceType, Validators.required],
      annual_cost: [this.currentProfile()!.annualCost, Validators.required],
      annual_hours: [{value: this.currentProfile()!.annualHours, disabled: true}],
      effectiveness: [this.currentProfile()!.effectivenessPercentage,
        [Validators.required, Validators.min(0), Validators.max(100)]],
      hours_per_day: [this.currentProfile()!.hoursPerDay,
        [Validators.required, Validators.min(0), Validators.max(24)]]
    })

    if (!this.profileForm.get('resource_type')?.value) {
      this.profileForm.get('annual_hours')?.enable();
    } else {
      this.profileForm.get('annual_hours')?.disable();
    }

    this.profileForm.get('resource_type')?.valueChanges.subscribe((value: boolean) => {
      if (value) {
        this.profileForm.get('annual_hours')?.reset({value: 0, disabled: true});
      } else {
        this.profileForm.get('annual_hours')?.enable();
      }
    });
  }

  private calculateDayRate(teamProfile: TeamProfile, profile: Profile) {
    let hourlyRate = teamProfile.allocatedCost! / teamProfile.allocatedHours!
    return hourlyRate * profile.hoursPerDay!;
  }
}
