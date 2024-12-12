import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatDialogActions, MatDialogContent} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {MatInput} from '@angular/material/input';
import {AuthenticationService} from '../../services/authentication.service';
import {Router} from '@angular/router';
import {SnackbarService} from '../../services/snackbar.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [
    FormsModule,
    MatDialogActions,
    MatButton,
    MatFormFieldModule,
    TranslateModule,
    MatDialogContent,
    MatInput,
    ReactiveFormsModule
  ],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css'
})
export class LoginPageComponent implements OnInit{
  userForm: FormGroup = new FormGroup({});

  constructor(private authenticationService: AuthenticationService,
              private snackbarService: SnackbarService,
              private translateService: TranslateService,
              private formBuilder: FormBuilder,
              private router: Router) {}

  ngOnInit(): void {
    this.userForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
      });
    }

  onSubmit() {
    this.authenticationService.authenticate(this.userForm.value).then((response) => {
      if (response) {
        this.snackbarService.openSnackBar(this.translateService.instant('SUCCESS_LOGIN'), true);
        this.router.navigate(['/']);
      } else {
        this.snackbarService.openSnackBar(this.translateService.instant('ERROR_LOGIN'), false);
      }
    });
  }
}
