import {Component} from '@angular/core';
import {MatButtonModule, MatIconButton} from '@angular/material/button';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {RouterLink} from '@angular/router';
import {MatIcon} from '@angular/material/icon';
import {MatTooltipModule} from '@angular/material/tooltip';
import {NgClass} from '@angular/common';
import {MenuService} from '../../services/menu.service';
import {CurrencyService} from '../../services/currency.service';

@Component({
  selector: 'app-sidemenu',
  standalone: true,
  imports: [
    MatButtonModule,
    TranslateModule,
    RouterLink,
    MatIconButton,
    MatIcon,
    MatTooltipModule,
    NgClass
  ],
  templateUrl: './sidemenu.component.html',
  styleUrl: './sidemenu.component.css'
})
export class SidemenuComponent {

  selectedCurrency: string | null;
  protected readonly localStorage = localStorage;

  constructor(private translate: TranslateService, private menuService: MenuService, private currencyService: CurrencyService) {
    this.selectedCurrency = localStorage.getItem('selectedCurrency');
    translate.setDefaultLang('en');
    menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    })
    currencyService.setCurrency("EUR");
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

  switchCurrency() {
    if (localStorage.getItem("selectedCurrency") == "EUR") {
      this.currencyService.setCurrency("USD");
      this.selectedCurrency = "USD";
    } else {
      this.currencyService.setCurrency("EUR");
      this.selectedCurrency = "EUR";
    }
  }

}
