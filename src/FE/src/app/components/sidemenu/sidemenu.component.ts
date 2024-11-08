import {Component} from '@angular/core';
import {MatButtonModule, MatIconButton} from '@angular/material/button';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {RouterLink} from '@angular/router';
import {MatIcon} from '@angular/material/icon';
import {MatSlideToggle} from '@angular/material/slide-toggle';
import {MatTooltipModule} from '@angular/material/tooltip';

@Component({
  selector: 'app-sidemenu',
  standalone: true,
  imports: [
    MatButtonModule,
    TranslateModule,
    RouterLink,
    MatIconButton,
    MatIcon,
    MatSlideToggle,
    MatTooltipModule
  ],
  templateUrl: './sidemenu.component.html',
  styleUrl: './sidemenu.component.css'
})
export class SidemenuComponent {
  constructor(private translate: TranslateService) {
    translate.setDefaultLang('en');
  }

  switchLanguage() {
    if (this.translate.currentLang == 'en') {
      this.translate.use('da');
    } else {
      this.translate.use('en');
    }
  }
}
