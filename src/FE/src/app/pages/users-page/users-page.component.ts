import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {DecimalPipe, NgClass, NgIf} from '@angular/common';
import {MatButton, MatIconButton} from '@angular/material/button';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable, MatTableDataSource
} from '@angular/material/table';
import {MatSuffix} from '@angular/material/form-field';
import {MatIcon} from '@angular/material/icon';
import {MatFormField, MatInput, MatLabel, MatPrefix} from '@angular/material/input';
import {MatOption} from '@angular/material/core';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatRadioButton, MatRadioGroup} from '@angular/material/radio';
import {MatSelect} from '@angular/material/select';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {UserService} from '../../services/user.service';
import {SnackbarService} from '../../services/snackbar.service';

@Component({
  selector: 'app-users-page',
  standalone: true,
  imports: [
    DecimalPipe,
    MatButton,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatFormField,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatOption,
    MatPrefix,
    MatProgressSpinner,
    MatRadioButton,
    MatRadioGroup,
    MatRow,
    MatRowDef,
    MatSelect,
    MatSuffix,
    MatTable,
    NgIf,
    ReactiveFormsModule,
    TranslateModule,
    MatHeaderCellDef,
    NgClass
  ],
  templateUrl: './users-page.component.html',
  styleUrl: './users-page.component.css'
})
export class UsersPageComponent implements OnInit{
  datasource: MatTableDataSource<any> = new MatTableDataSource();
  userForm: FormGroup = new FormGroup({});
  displayedColumns: string[] = [
    'username',
    'resetPassword',
    'delete'
  ];
  createUserForm: FormGroup = new FormGroup({});
  showResetPasswordFields = false;



  constructor(private formBuilder: FormBuilder,
              private changeDetectorRef: ChangeDetectorRef,
              private userService: UserService,
              private snackbarService: SnackbarService,
              private translateService: TranslateService) {
  }

  ngOnInit(): void {
      this.userForm = this.formBuilder.group({
        username: [''],
        delete: ['']
      });
      this.createUserForm = this.formBuilder.group({
        username: [''],
        password: [''],
        repeatPassword: [''],
        oldPassword: [''],
        newPassword: ['']
      });

      const data = [
        { username: 'John Doe' },
        { username: 'Jane Smith' },
        { username: 'Sam Wilson' },
      ];

    this.datasource.data = data;
  }

  applySearch(event: Event) {
    const searchValue = (event.target as HTMLInputElement).value;
    this.datasource.filter = searchValue.trim().toLowerCase();

    if (this.datasource.paginator) {
      this.datasource.paginator.firstPage();
    }
  }


  onSave() {

  }

  displayResetPassword() {
    this.showResetPasswordFields = !this.showResetPasswordFields;
    this.changeDetectorRef.detectChanges();
  }

  onResetPassword() {

  }

  async onDelete() {
    const result = await this.userService.deleteUser(this.userForm.value.delete);
    if(result){
      this.datasource.data = this.datasource.data.filter((user: any) => user.userId !== this.userForm.value.userId);
      this.datasource._updateChangeSubscription();
      this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_USER_DELETED'), true);
    } else {
      this.snackbarService.openSnackBar(this.translateService.instant('ERROR_USER_DELETED'), false);
    }
  }
}
