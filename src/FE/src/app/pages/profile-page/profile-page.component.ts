import {AfterViewInit, booleanAttribute, Component, OnInit} from '@angular/core';
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
import {NgIf} from '@angular/common';
import {MatMenuModule} from '@angular/material/menu';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {ProfileService} from '../../services/profile.service';
import {ActivatedRoute} from '@angular/router';
import {Geography, Profile, TeamProfiles} from '../../models';
import {GeographyService} from "../../services/geography.service";
import {TeamsService} from "../../services/teams.service";

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
    MatProgressSpinner
  ],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.css'
})
export class ProfilePageComponent implements OnInit, AfterViewInit {
  profileForm: FormGroup = new FormGroup({});
  currentProfile: Profile | undefined;
  locations: Geography[] = [];
  teams: TeamProfiles[] = [];
  statBoxes = {
    totalDayRate: '',
    totalHourlyRate: '',
    totalAnnualCost: '',
    totalAnnualHours: ''
  };
  loading: boolean = true;
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

  constructor(private fb: FormBuilder,
              private profileService: ProfileService,
              private geographyService: GeographyService,
              private teamsService: TeamsService,
              private route: ActivatedRoute) {
  }

  //#region mock data
  currencies: String[] = [
    'EUR',
    'USD'
  ]

  //#endregion

  async ngOnInit() {
    this.profileForm = this.fb.group({
      name: ['', Validators.required],
      location: ['', Validators.required],
      currency: ['', Validators.required],
      resource_type: [null, Validators.required],
      annual_cost: [''],
      annual_hours: [{value: '', disabled: true}],
      hours_per_day: [''],
      effectiveness: [''],
    });

    this.locations = await this.geographyService.getCountries();
    this.currentProfile = await this.profileService.getProfile(this.route.snapshot.paramMap.get('id')!);
    this.teams = await this.teamsService.getTeamProfiles(this.route.snapshot.paramMap.get('id')!);
    this.datasource.data = this.teams;

    this.profileForm = this.fb.group({
      name: [this.currentProfile!.name, Validators.required],
      location: [this.currentProfile?.countryId, Validators.required],
      currency: [this.currentProfile!.currency, Validators.required],
      resource_type: [this.currentProfile!.resourceType, Validators.required],
      annual_cost: [this.currentProfile!.annualCost],
      annual_hours: [{value: this.currentProfile!.annualHours, disabled: true}],
      effectiveness: [this.currentProfile!.effectivenessPercentage],
      hours_per_day: [this.currentProfile!.hoursPerDay]
    })

    if (!this.profileForm.get('resource_type')?.value) {
      this.profileForm.get('annual_hours')?.enable();
    } else {
      this.profileForm.get('annual_hours')?.disable();
    }

    this.statBoxes = {
      totalDayRate: (this.teams.reduce((sum, item) => sum + item.dayRateOnTeam!, 0)).toFixed(2),
      totalHourlyRate: (this.teams.reduce((sum, item) => sum + item.dayRateOnTeam!, 0) / this.currentProfile.hoursPerDay!).toFixed(2),
      totalAnnualCost: (this.teams.reduce((sum, item) => sum + item.annualCost!, 0)).toFixed(2),
      totalAnnualHours: (this.teams.reduce((sum, item) => sum + item.annualCost!, 0)).toFixed(2)
    }

    this.loading = false;

    this.profileForm.get('resource_type')?.valueChanges.subscribe((value: boolean) => {
      if (value) {
        this.profileForm.get('annual_hours')?.reset({value: 0, disabled: true});
      } else {
        this.profileForm.get('annual_hours')?.enable();
      }
    });
  }

  ngAfterViewInit() {

  }

  update() {
    //todo: update the profile in db
  }

  undo() {
    this.profileForm = this.fb.group({
      name: [this.currentProfile!.name],
      location: [this.currentProfile?.countryId],
      currency: [this.currentProfile!.currency],
      resource_type: [this.currentProfile!.resourceType],
      annual_cost: [this.currentProfile!.annualCost],
      annual_hours: [this.currentProfile!.annualHours],
      effectiveness: [this.currentProfile!.effectivenessPercentage],
      hours_per_day: [this.currentProfile!.hoursPerDay]
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
