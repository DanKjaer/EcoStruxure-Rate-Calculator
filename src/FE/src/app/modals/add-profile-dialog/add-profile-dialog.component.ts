import {AfterViewInit, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {TranslateModule} from '@ngx-translate/core';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatRadioModule} from '@angular/material/radio';
import {MatOption, MatSelect} from '@angular/material/select';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ProfileService} from '../../services/profile.service';
import {GeographyService} from '../../services/geography.service';
import {Geography, Profile} from '../../models';

@Component({
  selector: 'app-add-profile-dialog',
  standalone: true,
  imports: [
    TranslateModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatRadioModule,
    MatSelect,
    MatOption,
    ReactiveFormsModule
  ],
  templateUrl: './add-profile-dialog.component.html',
  styleUrl: './add-profile-dialog.component.css'
})
export class AddProfileDialogComponent implements OnInit {
  profileForm!: FormGroup;
  locations!: Geography[];
  @Output( ) profileAdded = new EventEmitter<Profile>();

  constructor(private formBuilder: FormBuilder,
              private profileService: ProfileService,
              public dialogRef: MatDialogRef<AddProfileDialogComponent>,
              public geographyService: GeographyService,
  ) {
  }

  async getCountries() {
    this.locations = await this.geographyService.getCountries();
  }

  currencies: String[] = [
    'EUR',
    'USD'
  ]

  ngOnInit() {
    this.profileForm = this.formBuilder.group({
      name: ['', Validators.required],
      countryId: ['', Validators.required],
      currency: ['', Validators.required],
      resource_type: [true, Validators.required],
      annual_cost: [''],
      annual_hours: [{value: '', disabled: true}],
      effectiveness: [''],
      hours_per_day: [''],
    });

    this.profileForm.get('resource_type')?.valueChanges.subscribe(value => {
      if (value == 1) {
        this.profileForm.get('annual_hours')?.disable();
      } else {
        this.profileForm.get('annual_hours')?.enable();
      }
    });
    this.getCountries();
  }

  addProfile() {
    //todo: add profile to db and update table
  }

  async onSave() {
    if (this.profileForm.valid) {
      let profile = {
        name: this.profileForm.value.name,
        countryId: this.profileForm.value.countryId,
        currency: this.profileForm.value.currency,
        resourceType: this.profileForm.value.resource_type,
        annualCost: this.profileForm.value.annual_cost,
        annualHours: this.profileForm.value.annual_hours,
        effectivenessPercentage: this.profileForm.value.effectiveness,
        hoursPerDay: this.profileForm.value.hours_per_day
      };
      const newProfile = await this.profileService.postProfile(profile);
      this.profileAdded.emit(newProfile);
    }
  }
}
