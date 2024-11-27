import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from '@angular/material/dialog';
import {MatDivider} from '@angular/material/divider';
import {MatFormField, MatLabel, MatSuffix} from '@angular/material/form-field';
import {MatIcon} from '@angular/material/icon';
import {MatInput} from '@angular/material/input';
import {MatList, MatListItem, MatListOption, MatSelectionList, MatSelectionListChange} from '@angular/material/list';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {Profile, Team, TeamProfiles} from '../../models';
import {ProfileService} from '../../services/profile.service';
import {SnackbarService} from '../../services/snackbar.service';
import {TeamsService} from '../../services/teams.service';
import {TeamProfileService} from '../../services/team-profile.service';
import {NgIf} from "@angular/common";

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
        TranslateModule,
        NgIf
    ],
    templateUrl: './add-to-team-dialog.component.html',
    styleUrl: './add-to-team-dialog.component.css'
})
export class AddToTeamDialogComponent implements OnInit {
    profileForm!: FormGroup;
    profileList: Profile[] = [];
    selectedProfiles: TeamProfiles[] = [];
    @Output() AddToTeamProfiles = new EventEmitter<TeamProfiles>();
    @Input() teamProfiles!: TeamProfiles;


    constructor(
        private formBuilder: FormBuilder,
        private profileService: ProfileService,
        private snackBar: SnackbarService,
        private translate: TranslateService,
        private teamProfilesService: TeamProfileService,
        private teamService: TeamsService
    ) {}

    async ngOnInit(): Promise<void> {
        this.profileForm = this.formBuilder.group({
            profiles: [[], Validators.required],
        })
        this.profileList = await this.profileService.getProfiles();
    }

    async onSave() {
       /* let profiles = this.selectedProfiles;

        profiles.forEach((profile) => {
            let teamProfile: TeamProfiles = {
                profileId: profile.profileId!,
                name: profile.name!,
                annualCost: profile.annualCost!,
                annualHours: profile.annualHours!,
                costAllocation: 100,
                hourAllocation: 100,
                geography: profile.geography!,
            };

            this.teamProfiles.push(teamProfile);
        });
            try {
                const updatedTeamProfiles = await this.teamProfilesService.putTeamProfile(this.teamProfiles);

                if (updatedTeamProfiles != undefined) {
                    this.AddToTeamProfiles.emit(updatedTeamProfiles);
                    this.teamProfiles = updatedTeamProfiles;
                    this.snackBar.openSnackBar(this.translate.instant('SUCCESS_TEAM_UPDATED'), true);
                } else {
                    this.snackBar.openSnackBar(this.translate.instant('ERROR_TEAM_UPDATED'), false);
                }
            } catch (error: any) {
                this.snackBar.openSnackBar(this.translate.instant('ERROR_TEAM_UPDATED'), false);
            }*/
        }

    onSelectionChange($event: MatSelectionListChange) {
        this.selectedProfiles = $event.source.selectedOptions.selected.map(profile => profile.value);
    }

    isProfileSelected(profile: Profile) {
        return this.selectedProfiles.some(selectedProfile => selectedProfile.profileId === profile.profileId);
    }
}
