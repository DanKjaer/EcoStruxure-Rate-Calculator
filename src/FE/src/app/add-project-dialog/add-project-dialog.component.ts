import {ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogModule,
  MatDialogRef
} from '@angular/material/dialog';
import {MatFormField, MatFormFieldModule, MatLabel} from '@angular/material/form-field';
import {MatOption, MatSelect} from '@angular/material/select';
import {TranslateModule} from '@ngx-translate/core';
import {MatNativeDateModule, provideNativeDateAdapter} from '@angular/material/core';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatInput} from '@angular/material/input';
import {Profile, Team} from '../models';
import {MatListOption, MatSelectionList, MatSelectionListChange} from '@angular/material/list';
import {ProfileService} from '../services/profile.service';
import {
  MatDatepickerInput,
  MatDatepickerModule,
  MatDatepickerToggle,
  MatDateRangeInput,
  MatDateRangePicker
} from '@angular/material/datepicker';

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
    MatSelectionList,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatDateRangeInput,
    MatDateRangePicker,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
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
              public dialogRef: MatDialogRef<AddProjectDialogComponent>,
              private profileService: ProfileService) {}

  async ngOnInit() {
    this.projectForm = this.formBuilder.group({
      project_name: [''],
      project_markup: [''],
      project_gm: [''],
      start_date: new FormControl<Date | null>(null),
      end_date: new FormControl<Date | null>(null),
      project_description: [''],
    })

    this.profileForm = this.formBuilder.group({
      name: ['', Validators.required],
      profiles: [[], Validators.required]
    });

    this.profileList = await this.profileService.getProfiles();
  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedProfiles = $event.source.selectedOptions.selected.map(profile => profile.value);
  }

  onAddProject(){
    this.projectAdded.emit(this.projectForm.value);
  }
}
