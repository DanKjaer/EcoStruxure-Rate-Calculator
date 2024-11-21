import {
  AfterViewInit,
  booleanAttribute,
  ChangeDetectorRef,
  Component,
  computed,
  OnInit,
  signal,
  Signal, WritableSignal
} from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
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
import {Currency, Geography, Profile, TeamProfiles} from '../../models';
import {GeographyService} from "../../services/geography.service";
import {TeamsService} from "../../services/teams.service";
import {MenuService} from '../../services/menu.service';
import {CurrencyService} from '../../services/currency.service';

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
  teams: WritableSignal<TeamProfiles[]> = signal<TeamProfiles[]>([]);
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
  currencies: Currency[] | undefined;

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

    const totalDayRate = teams.reduce((sum, item) => sum + item.dayRateOnTeam!, 0);
    const totalHourlyRate = totalDayRate / profile.hoursPerDay!;
    const totalAnnualCost = teams.reduce((sum, item) => sum + item.annualCost, 0);
    const totalAnnualHours = teams.reduce((sum, item) => sum + item.annualHours, 0);

    return { totalDayRate, totalHourlyRate, totalAnnualCost, totalAnnualHours };
  });

  constructor(private fb: FormBuilder,
              private profileService: ProfileService,
              private geographyService: GeographyService,
              private teamsService: TeamsService,
              private route: ActivatedRoute,
              private menuService: MenuService,
              protected currencyService: CurrencyService,
              private changeDetection: ChangeDetectorRef) { }

  async ngOnInit() {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {

      this.isMenuOpen = isOpen;
    });
    this.profileForm = this.fb.group({
      name: ['', Validators.required],
      location: ['', Validators.required],
      currency: ['', Validators.required],
      resource_type: [null, Validators.required],
      annual_cost: [''],
      annual_hours: [{value: '', disabled: true}],
      hours_per_day: [''],
      effectiveness: ['']
    });

    this.currencies = await this.currencyService.getCurrencies();

    this.locations = await this.geographyService.getCountries();

    let id = this.route.snapshot.paramMap.get('id')!;

    this.currentProfile.set(await this.profileService.getProfile(id));
    this.prepProfileForm();
    await this.prepTeamList(id);
    // this.setStatBoxes();

    this.loading = false;
  }

  private async prepTeamList(id: string) {
    let response = await this.teamsService.getTeamProfiles(id);
    this.teams.set(response);
    this.datasource.data = response;
  }

  private prepProfileForm() {
    this.profileForm = this.fb.group({
      name: [this.currentProfile!.name, Validators.required],
      location: [this.currentProfile()!.geography.id, Validators.required],
      currency: [this.currentProfile()!.currency, Validators.required],
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
    //todo: update the profile in db
    this.currentProfile()!.name = this.profileForm.get('name')?.value;
    this.currentProfile()!.geography = this.locations.find((location) => {
      return location.id === this.profileForm.value.location
    })!;
    this.currentProfile()!.currency = this.profileForm.get('currency')?.value;
    this.currentProfile()!.resourceType = this.profileForm.get('resource_type')?.value;
    this.currentProfile()!.annualCost = this.profileForm.get('annual_cost')?.value;
    this.currentProfile()!.annualHours = this.profileForm.get('annual_hours')?.value;
    this.currentProfile()!.effectivenessPercentage = this.profileForm.get('effectiveness')?.value;
    this.currentProfile()!.hoursPerDay = this.profileForm.get('hours_per_day')?.value;

    let response = await this.profileService.putProfile(this.currentProfile()!);
    this.currentProfile.set(response);

    this.prepTeamList(this.currentProfile()!.profileId!);
    this.prepProfileForm();
  }

  undo() {
    this.profileForm = this.fb.group({
      name: [this.currentProfile()!.name],
      location: [this.currentProfile()!.geography.id],
      currency: [this.currentProfile()!.currency],
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

}
