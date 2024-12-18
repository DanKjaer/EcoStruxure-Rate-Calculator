import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {NgClass, NgIf} from '@angular/common';
import {MatButton, MatIconButton} from '@angular/material/button';
import {
  MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatIcon} from '@angular/material/icon';
import {MatFormField, MatInput, MatLabel, MatPrefix, MatSuffix} from '@angular/material/input';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {UserService} from '../../services/user.service';
import {SnackbarService} from '../../services/snackbar.service';
import {User} from '../../models';
import {MenuService} from '../../services/menu.service';
import {ConfirmDialogComponent} from '../../modals/confirm-dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-users-page',
  standalone: true,
  imports: [
    MatButton,
    MatTableModule,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatPrefix,
    NgIf,
    ReactiveFormsModule,
    TranslateModule,
    NgClass,
    MatSuffix,
    FormsModule,
  ],
  templateUrl: './users-page.component.html',
  styleUrl: './users-page.component.css'
})
export class UsersPageComponent implements OnInit {
  datasource: MatTableDataSource<any> = new MatTableDataSource();
  selectedRow: User | null = null;
  displayedColumns: string[] = [
    'username',
    'changePassword',
    'delete'
  ];
  createUserForm: FormGroup = new FormGroup({});
  changePasswordForm: FormGroup = new FormGroup({});
  showResetPasswordFields = false;
  isMenuOpen: boolean | undefined;
  value: string = '';

  constructor(private formBuilder: FormBuilder,
              private changeDetectorRef: ChangeDetectorRef,
              private userService: UserService,
              private snackbarService: SnackbarService,
              private translateService: TranslateService,
              private menuService: MenuService,
              private dialog: MatDialog) {
  }

  async ngOnInit(): Promise<void> {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    });

    this.createUserForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(8)]],
      repeatPassword: ['', [Validators.required, Validators.minLength(8)]],
    });

    this.changePasswordForm = this.formBuilder.group({
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      repeatPassword: ['', [Validators.required, Validators.minLength(8)]]
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

  clearSearch() {
    this.value = '';
    this.applySearch({target: {value: ''}} as unknown as Event);
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
        this.datasource.data.push(savedUserResult);
        this.datasource._updateChangeSubscription();
      })
      .catch((error) => {
        this.snackbarService.openSnackBar(this.translateService.instant('ERROR_USER_CREATED'), false);
      });
  }

  showChangePassword(row: User) {
    this.selectedRow = row;
    this.showResetPasswordFields = !this.showResetPasswordFields;
    this.changeDetectorRef.detectChanges();
  }

  onChangePassword() {
    const newPassword = this.changePasswordForm.get('newPassword')?.value;
    const repeatPassword = this.changePasswordForm.get('repeatPassword')?.value;

    if (newPassword === repeatPassword) {
      const user = {
        userId: this.selectedRow!.userId,
        username: this.selectedRow!.username,
        password: newPassword,
      };

      this.userService.putUser(user)
        .then((savedUserResult) => {
          this.datasource.data = this.datasource.data.map((user: User) => {
            if (user.userId === savedUserResult.userId) {
              return savedUserResult;
            }
            return user;
          });
          this.changePasswordForm.reset();
          this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_PASSWORD_CHANGED'), true);
          this.datasource._updateChangeSubscription();
          this.showResetPasswordFields = false;
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
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: this.translateService.instant('CONFIRM_DELETE_USER_TITLE'),
        message: this.translateService.instant('CONFIRM_DELETE_MESSAGE') + row.username + '?'
      },
      maxWidth: '15vw',
      minWidth: '15vw',
    });
    dialogRef.afterClosed().subscribe(async (confirmed: boolean) => {
      if (confirmed) {
        const result = await this.userService.deleteUser(row.userId!);
        if (result) {
          const updatedData = this.datasource.data.filter(user => user !== row);
          this.datasource.data = [...updatedData];
          this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_USER_DELETED'), true);
        } else {
          this.snackbarService.openSnackBar(this.translateService.instant('ERROR_USER_DELETED'), false);
        }
      }
    });
  }

  selectRow(row: User) {
    this.selectedRow = row;
  }
}
