import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {NgClass, NgIf} from '@angular/common';
import {MatButton, MatIconButton} from '@angular/material/button';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource
} from '@angular/material/table';
import {MatIcon} from '@angular/material/icon';
import {MatFormField, MatInput, MatLabel, MatPrefix} from '@angular/material/input';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {UserService} from '../../services/user.service';
import {SnackbarService} from '../../services/snackbar.service';
import {Profile, User} from '../../models';

@Component({
  selector: 'app-users-page',
  standalone: true,
  imports: [
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
    MatPrefix,
    MatRow,
    MatRowDef,
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
export class UsersPageComponent implements OnInit {
  datasource: MatTableDataSource<any> = new MatTableDataSource();
  selectedRow: User | null = null;
  userForm: FormGroup = new FormGroup({});
  displayedColumns: string[] = [
    'username',
    'changePassword',
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

  async ngOnInit(): Promise<void> {
    this.userForm = this.formBuilder.group({
      username: [''],
      delete: ['']
    });
    this.createUserForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      repeatPassword: ['', Validators.required],
      oldPassword: [''],
      newPassword: ['']
    });

    this.datasource.data = await this.userService.getUsers();
  }

  applySearch(event: Event) {
    const searchValue = (event.target as HTMLInputElement).value;
    this.datasource.filter = searchValue.trim().toLowerCase();

    if (this.datasource.paginator) {
      this.datasource.paginator.firstPage();
    }
  }


  onSave() {
    const password = this.createUserForm.get('password')?.value;
    const repeatPassword = this.createUserForm.get('repeatPassword')?.value;

    if (password !== repeatPassword) {
      this.snackbarService.openSnackBar(this.translateService.instant('ERROR_PASSWORDS_DONT_MATCH'), false);
      return;
    }

    const user = {
      username: this.createUserForm.get('username')?.value,
      password: this.createUserForm.get('password')?.value,
      repeatPassword: this.createUserForm.get('repeatPassword')?.value
    };

    this.userService.postUser(user)
      .then((savedUserResult) => {
        this.createUserForm.reset();
        this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_USER_CREATED'), true);
        this.datasource._updateChangeSubscription();
      })
      .catch((error) => {
        this.snackbarService.openSnackBar(this.translateService.instant('ERROR_USER_CREATED'), false);
      });
  }

  showChangePassword(row: User) {
    console.log('row', row);
    this.selectedRow = row;
    this.showResetPasswordFields = !this.showResetPasswordFields;
    this.changeDetectorRef.detectChanges();
  }

  onChangePassword() {
    const newPassword = this.createUserForm.get('newPassword')?.value;
    const repeatPassword = this.createUserForm.get('repeatPassword')?.value;

    console.log('newPassword', newPassword);
    console.log('repeatPassword', repeatPassword);

    if (newPassword === repeatPassword) {
      const user = {
        userId: this.selectedRow!.userId,
        username: this.selectedRow!.username,
        password: newPassword,
      };

      console.log('user: ',user)
      this.userService.putUser(user)
        .then((savedUserResult) => {
          this.datasource.data = this.datasource.data.map((user: User) => {
            if (user.userId === savedUserResult.userId) {
              return savedUserResult;
            }
            return user;
          });
          this.createUserForm.reset();
          this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_PASSWORD_CHANGED'), true);
          this.datasource._updateChangeSubscription();
        })
        .catch((error) => {
          this.snackbarService.openSnackBar(this.translateService.instant('ERROR_PASSWORD_CHANGED'), false);
        });
    } else {
      this.snackbarService.openSnackBar(this.translateService.instant('ERROR_PASSWORDS_DIFFERENT'), false);
      return;
    }
  }

  async onDelete(row: any) {
    const result = await this.userService.deleteUser(row.id!);
    if (result) {
      const updatedData = this.datasource.data.filter(user => user !== row);
      this.datasource.data = [...updatedData];
      this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_USER_DELETED'), true);
    } else {
      this.snackbarService.openSnackBar(this.translateService.instant('ERROR_USER_DELETED'), false);
    }
  }

  selectRow(row: User) {
    this.selectedRow = row;
  }
}
