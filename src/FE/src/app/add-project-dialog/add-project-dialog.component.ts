import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {ProjectService} from '../services/project.service';
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogModule,
  MatDialogRef
} from '@angular/material/dialog';
import {MatFormField, MatLabel} from '@angular/material/form-field';
import {MatOption, MatSelect} from '@angular/material/select';
import {TranslateModule} from '@ngx-translate/core';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatInput} from '@angular/material/input';
import {Profile, Team} from '../models';
import {MatListOption, MatSelectionList, MatSelectionListChange} from '@angular/material/list';

@Component({
  selector: 'app-add-project-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogContent,
    MatFormField,
    MatSelect,
    TranslateModule,
    ReactiveFormsModule,
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatIcon,
    MatInput,
    MatLabel,
    MatOption,
    MatDialogModule,
    MatListOption,
    MatSelectionList
  ],
  templateUrl: './add-project-dialog.component.html',
  styleUrl: './add-project-dialog.component.css'
})
export class AddProjectDialogComponent implements OnInit{
  projectForm!: FormGroup;
  profileForm!: FormGroup;
  profileList: Profile[] = [];
  selectedProfiles: Profile[] = [];
  @Output() projectAdded = new EventEmitter<Team>();


  constructor(private formBuilder: FormBuilder,
              public dialogRef: MatDialogRef<AddProjectDialogComponent>) {}

  ngOnInit() {
    this.projectForm = this.formBuilder.group({
      project_name: [''],
      project_markup: [''],
      project_gm: [''],
      end_date: [''],
      start_date: [''],
      project_description: [''],
    })

    this.profileForm = this.formBuilder.group({
      name: [''],
      profiles: ['']
    });
  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedProfiles = $event.source.selectedOptions.selected.map(profile => profile.value);
  }

  onAddProject(){
    this.projectAdded.emit(this.projectForm.value);
  }
}
