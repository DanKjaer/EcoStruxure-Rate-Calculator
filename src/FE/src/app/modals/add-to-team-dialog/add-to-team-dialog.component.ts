import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from '@angular/material/dialog';
import {MatDivider} from '@angular/material/divider';
import {MatFormField, MatLabel, MatSuffix} from '@angular/material/form-field';
import {MatIcon} from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import {MatList, MatListItem, MatListOption, MatSelectionList, MatSelectionListChange} from '@angular/material/list';
import {TranslateModule} from '@ngx-translate/core';
import { TeamProfiles} from '../../models';
import {ProfileService} from '../../services/profile.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-add-to-team-dialog',
  standalone: true,
  imports: [
    FormsModule,
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    MatDivider,
    MatFormField,
    MatIcon,
    MatInput,
    MatLabel,
    MatList,
    MatListItem,
    MatListOption,
    MatSelectionList,
    MatSuffix,
    ReactiveFormsModule,
    TranslateModule
  ],
  templateUrl: './add-to-team-dialog.component.html',
  styleUrl: './add-to-team-dialog.component.css'
})
export class AddToTeamDialogComponent implements OnInit{
  profileForm!: FormGroup;
  profileList: TeamProfiles[] = [];
  selectedProfiles: TeamProfiles[] = [];


  constructor(
    private formBuilder: FormBuilder,
    private profileService: ProfileService,
    private route: ActivatedRoute,
  ) {
  }

  async ngOnInit(): Promise<void>{
        this.profileForm = this.formBuilder.group({
          profiles: [[], Validators.required],
        })

    this
    let profiles = await this.profileService.getProfiles();
    }

  onSave() {

  }

  onSelectionChange($event: MatSelectionListChange) {
    this.selectedProfiles = $event.source.selectedOptions.selected.map(profile => profile.value);
  }
}
