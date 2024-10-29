import {Component, OnInit} from '@angular/core';
import {MatDialogModule} from '@angular/material/dialog';
import {TranslateModule} from '@ngx-translate/core';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatRadioModule} from '@angular/material/radio';
import {MatOption, MatSelect} from '@angular/material/select';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';

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

  constructor(private fb: FormBuilder) {
  }

  locations: String[] = [
    'Denmark',
    'England',
    'Germany'
  ]

  currencies: String[] = [
    'EUR',
    'USD'
  ]

  ngOnInit() {
    this.profileForm = this.fb.group({
      name: ['', Validators.required],
      location: ['', Validators.required],
      currency: ['', Validators.required],
      resource_type: ['', Validators.required],
      annual_cost: [1],
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

  addProfile() {
    //todo: add profile to db and update table
  }
}
