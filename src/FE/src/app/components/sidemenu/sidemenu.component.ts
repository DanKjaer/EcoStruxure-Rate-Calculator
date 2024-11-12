import {Component} from '@angular/core';
import {MatButtonModule, MatIconButton} from '@angular/material/button';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {RouterLink} from '@angular/router';
import {MatIcon} from '@angular/material/icon';
import {MatSlideToggle} from '@angular/material/slide-toggle';
import {MatTooltipModule} from '@angular/material/tooltip';
import {NgClass} from '@angular/common';
import {MenuService} from '../../services/menu.service';

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
    MatTooltipModule,
    NgClass
  ],
  templateUrl: './sidemenu.component.html',
  styleUrl: './sidemenu.component.css'
})
export class SidemenuComponent {
  constructor(private translate: TranslateService, private menuService: MenuService) {
    translate.setDefaultLang('en');
    this.menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    })
  }

  isMenuOpen = true;

  toggleMenu(): void {
    this.menuService.toggleMenu();
  }

  switchLanguage() {
    if (this.translate.currentLang == 'en') {
      this.translate.use('da');
    } else {
      this.translate.use('en');
    }
  }
}
