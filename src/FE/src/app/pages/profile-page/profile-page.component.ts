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
import {Profile} from '../../models';

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
export class ProfilePageComponent implements OnInit {
  profileForm: FormGroup = new FormGroup({});
  currentProfile: Profile | undefined;

  constructor(private fb: FormBuilder, private profileService: ProfileService, private route: ActivatedRoute) {
  }

  //#region mock data
  locations: String[] = [
    'Denmark',
    'England',
    'Germany'
  ]

  currencies: String[] = [
    'EUR',
    'USD'
  ]
  //#endregion

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

  loading: boolean = true;

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

    this.currentProfile = await this.profileService.getProfile(this.route.snapshot.paramMap.get('id')!);

    if (this.currentProfile) {
      this.profileForm.patchValue({
        name: [this.currentProfile.name],
        location: [''],
        currency: [this.currentProfile.currency],
        resource_type: [this.currentProfile.resourceType],
        annual_cost: [this.currentProfile.annualCost],
        annual_hours: [this.currentProfile.annualHours],
        hours_per_day: [this.currentProfile.hoursPerDay],
        effectiveness: [this.currentProfile.effectivenessPercentage],
      })
    }

    this.profileForm.get('resource_type')?.valueChanges.subscribe(value => {
      if (value === true) {
        this.profileForm.get('annual_hours')?.disable();
      } else {
        this.profileForm.get('annual_hours')?.enable();
      }
    });
  }

  update() {
    //todo: update the profile in db
  }

  undo() {
    this.profileForm = this.fb.group({
      name: [this.currentProfile!.name, Validators.required],
      location: ['', Validators.required],
      currency: [this.currentProfile!.currency, Validators.required],
      resource_type: [this.currentProfile!.resourceType, Validators.required],
      annual_cost: [this.currentProfile!.annualCost],
      annual_hours: [{value: this.currentProfile!.annualHours, disabled: true}],
      effectiveness: [this.currentProfile!.effectivenessPercentage],
      hours_per_day: [this.currentProfile!.hoursPerDay]
    })
  }
}
