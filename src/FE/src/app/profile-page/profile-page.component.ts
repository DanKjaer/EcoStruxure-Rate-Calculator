import {Component, OnInit} from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatOption} from '@angular/material/core';
import {MatRadioModule} from '@angular/material/radio';
import {MatSelect} from '@angular/material/select';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';

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
    ReactiveFormsModule
  ],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.css'
})
export class ProfilePageComponent implements OnInit {
  profileForm!: FormGroup;

  constructor(private fb: FormBuilder) {
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

  ngOnInit() {
    this.profileForm = this.fb.group({
      name: ['', Validators.required],
      location: ['', Validators.required],
      currency: ['', Validators.required],
      resource_type: [1, Validators.required],
      annual_cost: [0],
      annual_hours: [{value: 0, disabled: true}],
      effectiveness: [0],
      hours_per_day: [0],
    })

    this.profileForm.get('resource_type')?.valueChanges.subscribe(value => {
      if (value == 1) {
        this.profileForm.get('annual_hours')?.disable();
      } else {
        this.profileForm.get('annual_hours')?.enable();
      }
    });
  }

}
