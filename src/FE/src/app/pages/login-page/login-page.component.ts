import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {MatDialogActions, MatDialogContent} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {TranslateModule} from '@ngx-translate/core';
import {MatInput} from '@angular/material/input';

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
    MatInput
  ],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css'
})
export class LoginPageComponent {
  username: any;
  password: any;

  onSubmit() {

  }
}
