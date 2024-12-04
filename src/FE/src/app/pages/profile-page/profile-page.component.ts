import {
  Component,
  computed,
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
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {DecimalPipe, NgClass, NgIf} from '@angular/common';
import {MatMenuModule} from '@angular/material/menu';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {ProfileService} from '../../services/profile.service';
import {ActivatedRoute} from '@angular/router';
import {Currency, Geography, Profile, TeamProfile} from '../../models';
import {GeographyService} from "../../services/geography.service";
import {TeamsService} from "../../services/teams.service";
import {MenuService} from '../../services/menu.service';
import {CurrencyService} from '../../services/currency.service';
import {SnackbarService} from '../../services/snackbar.service';

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
    DecimalPipe
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
    'options'
  ];

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
    }
    const totalDayRate = teams.reduce((sum, item) => sum + this.calculateDayRate(item, profile), 0);
    const totalHourlyRate = totalDayRate / profile.hoursPerDay!;
    const totalAnnualCost = teams.reduce((sum, item) => sum + item.allocatedCost!, 0);
    const totalAnnualHours = teams.reduce((sum, item) => sum + item.allocatedHours!, 0);

    return { totalDayRate, totalHourlyRate, totalAnnualCost, totalAnnualHours };
  });

  constructor(private fb: FormBuilder,
              private profileService: ProfileService,
              private geographyService: GeographyService,
              private teamsService: TeamsService,
              private route: ActivatedRoute,
              private menuService: MenuService,
              protected currencyService: CurrencyService,
              private snackBar: SnackbarService,
              private translate: TranslateService) { }

  async ngOnInit() {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {

      this.isMenuOpen = isOpen;
    });
    this.profileForm = this.fb.group({
      name: ['', Validators.required],
      location: ['', Validators.required],
      resource_type: [null, Validators.required],
      annual_cost: [''],
      annual_hours: [{value: '', disabled: true}],
      hours_per_day: [''],
      effectiveness: ['']
    });

    this.locations = await this.geographyService.getCountries();

    let id = this.route.snapshot.paramMap.get('id')!;

    this.currentProfile.set(await this.profileService.getProfile(id));
    this.prepProfileForm();
    await this.prepTeamList(id);

    this.loading = false;
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
      annual_cost: [this.currentProfile()!.annualCost],
      annual_hours: [{value: this.currentProfile()!.annualHours, disabled: true}],
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

  applySearch(event: Event) {
    const searchValue = (event.target as HTMLInputElement).value;
    this.datasource.filter = searchValue.trim().toLowerCase();

    if (this.datasource.paginator) {
      this.datasource.paginator.firstPage();
    }
  }

  async update() {
    this.loading = true;
    //todo: update the profile in db
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
      this.snackBar.openSnackBar(this.translate.instant('REASON_UPDATED_PROFILE'), true);
    } catch (e) {
      this.snackBar.openSnackBar(this.translate.instant('FAILED_UPDATED_PROFILE'), false);
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

  private calculateDayRate(teamProfile: TeamProfile, profile: Profile) {
    let hourlyRate = teamProfile.allocatedCost! / teamProfile.allocatedHours!
    return hourlyRate * profile.hoursPerDay!;
  }
}
