import {Component, OnInit} from '@angular/core';
import {MatDialogModule} from '@angular/material/dialog';
import {TranslateModule} from '@ngx-translate/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatFormField, MatLabel, MatPrefix} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatListModule} from '@angular/material/list';

@Component({
  selector: 'app-add-teams-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    TranslateModule,
    FormsModule,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    MatButton,
    MatIcon,
    MatListModule,
    MatPrefix
  ],
  templateUrl: './add-teams-dialog.component.html',
  styleUrl: './add-teams-dialog.component.css'
})
export class AddTeamsDialogComponent implements OnInit {
  teamForm!: FormGroup;

  constructor(private fb: FormBuilder) {
  }

  profileList: string[] = ['Lars Jensen', 'Jens Jensen', 'Lars Larsen', 'Jens Larsen', 'Hans Hansen', 'Peter Petersen', 'Hans Larsen', 'Peter Jensen'];

  ngOnInit() {
    this.teamForm = this.fb.group({
      name: ['', Validators.required],
      profiles: [[], Validators.required]
    })
  }
}
