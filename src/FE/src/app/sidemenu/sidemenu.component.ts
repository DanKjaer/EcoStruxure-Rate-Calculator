import { Component } from '@angular/core';
import {MatButton} from '@angular/material/button';
import {TranslateModule, TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-sidemenu',
  standalone: true,
  imports: [
    MatButton,
    TranslateModule
  ],
  templateUrl: './sidemenu.component.html',
  styleUrl: './sidemenu.component.css'
})
export class SidemenuComponent {
  constructor(private translate: TranslateService) {
    translate.setDefaultLang('en');
  }

  switchLanguage(lang: string) {
    this.translate.use(lang);
  }
}
