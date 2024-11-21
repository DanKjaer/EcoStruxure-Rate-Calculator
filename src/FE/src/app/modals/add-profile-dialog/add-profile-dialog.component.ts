import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatRadioModule} from '@angular/material/radio';
import {MatOption, MatSelect} from '@angular/material/select';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ProfileService} from '../../services/profile.service';
import {GeographyService} from '../../services/geography.service';
import {Currency, Geography, Profile} from '../../models';
import {SnackbarService} from '../../services/snackbar.service';
import {CurrencyService} from '../../services/currency.service';

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
  profileForm: FormGroup = new FormGroup({});
  locations!: Geography[];
  currencies!: Currency[];
  @Output() profileAdded = new EventEmitter<Profile>();

  constructor(private formBuilder: FormBuilder,
              private profileService: ProfileService,
              public dialogRef: MatDialogRef<AddProfileDialogComponent>,
              public geographyService: GeographyService,
              private snackBar: SnackbarService,
              private translate: TranslateService,
              protected currencyService: CurrencyService,
  ) {
  }

  async getCountries() {
    this.locations = await this.geographyService.getCountries();
  }


  async ngOnInit() {
    this.profileForm = this.formBuilder.group({
      name: ['', Validators.required],
      geography: ['', Validators.required],
      currency: ['', Validators.required],
      resource_type: [true, Validators.required],
      annual_cost: [''],
      annual_hours: [{value: '', disabled: true}],
      effectiveness: [''],
      hours_per_day: [''],
    });

    this.currencies = await this.currencyService.getCurrencies();

    this.profileForm.get('resource_type')?.valueChanges.subscribe(value => {
      if (value == 1) {
        this.profileForm.get('annual_hours')?.disable();
      } else {
        this.profileForm.get('annual_hours')?.enable();
      }
    });
    await this.getCountries();
  }

  async onSave() {
    console.log(this.profileForm.value.geography);
    if (this.profileForm.valid) {
      let profileDTO: Profile = {
          name: this.profileForm.value.name,
          geography: this.profileForm.value.geography,
          currency: this.profileForm.value.currency.currencyCode,
          resourceType: this.profileForm.value.resource_type,
          annualCost: this.currencyService.convert(this.profileForm.value.annual_cost, this.profileForm.value.currency, "EUR"),
          annualHours: this.profileForm.value.annual_hours || 0,
          effectivenessPercentage: this.profileForm.value.effectiveness || 0,
          hoursPerDay: this.profileForm.value.hours_per_day || 0
      };
      const newProfile = await this.profileService.postProfile(profileDTO);
      if (newProfile.profileId != undefined) {
        this.profileAdded.emit(newProfile);
        this.snackBar.openSnackBar(this.translate.instant('SUCCESS_PROFILE_CREATED'), true);
      } else {
        this.snackBar.openSnackBar(this.translate.instant('ERROR_PROFILE_CREATED'), false);
      }
    }
  }
}
