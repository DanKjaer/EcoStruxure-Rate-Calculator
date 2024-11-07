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
import {MatNativeDateModule} from '@angular/material/core';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatInput} from '@angular/material/input';
import {Profile, Project} from '../models';
import {MatListOption, MatSelectionList, MatSelectionListChange} from '@angular/material/list';
import {ProfileService} from '../services/profile.service';
import {
  MatDatepickerInput,
  MatDatepickerModule,
  MatDatepickerToggle,
  MatDateRangeInput,
  MatDateRangePicker
} from '@angular/material/datepicker';
import {ProjectService} from '../services/project.service';

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
export class AddProjectDialogComponent implements OnInit {
  projectForm!: FormGroup;
  profileForm!: FormGroup;
  profileList: Profile[] = [];
  selectedProfiles: Profile[] = [];
  @Output() projectAdded = new EventEmitter<Project>();


  constructor(private formBuilder: FormBuilder,
              public dialogRef: MatDialogRef<AddProjectDialogComponent>,
              private profileService: ProfileService,
              private projectService: ProjectService) {
  }

  async ngOnInit() {
    this.projectForm = this.formBuilder.group({
      projectName: [''],
      projectMarkup: [''],
      projectGrossMargin: [''],
      startDate: new FormControl<Date | null>(null),
      endDate: new FormControl<Date | null>(null),
      projectDescription: [''],
    })

    this.profileForm = this.formBuilder.group({
      profiles: [[], Validators.required]
    });

    this.profileList = await this.profileService.getProfiles();
  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedProfiles = $event.source.selectedOptions.selected.map(profile => profile.value);
  }

  async onAddProject() {
    if (this.projectForm.valid) {
      let project = {
        projectName: this.projectForm.value.projectName,
        projectDescription: this.projectForm.value.projectDescription,
        projectMembers: this.profileForm.value.profiles,
        startDate: this.projectForm.value.startDate,
        endDate: this.projectForm.value.endDate,
        projectMarkup: this.projectForm.value.projectMarkup,
        projectGrossMargin: this.projectForm.value.projectGrossMargin,
      }
      const newProject = await this.projectService.postProject(project)
      this.projectAdded.emit(newProject);
    }
  }
}