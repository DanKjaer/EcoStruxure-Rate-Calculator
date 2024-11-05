import {Component, OnInit} from '@angular/core';
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

  update() {
    //todo: update the profile in db
  }

  undo() {
    //todo: reset the values
  }
}
