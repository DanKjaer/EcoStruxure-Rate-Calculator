import {Component, OnInit} from '@angular/core';
import {MatButtonModule, MatIconButton} from '@angular/material/button';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {NavigationEnd, Router, RouterLink} from '@angular/router';
import {MatIcon} from '@angular/material/icon';
import {MatTooltipModule} from '@angular/material/tooltip';
import {NgClass, NgIf} from '@angular/common';
import {MenuService} from '../../services/menu.service';
import {CurrencyService} from '../../services/currency.service';
import {ExportService} from '../../services/export.service';

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
    NgClass,
    NgIf
  ],
  templateUrl: './sidemenu.component.html',
  styleUrl: './sidemenu.component.css'
})
export class SidemenuComponent implements OnInit {

  selectedCurrency: string | null;
  protected readonly localStorage = localStorage;
  isMenuOpen = true;
  showSidemenu: boolean = true;

  constructor(private translate: TranslateService,
              private menuService: MenuService,
              private currencyService: CurrencyService,
              private exportService: ExportService,
              private router: Router) {
    this.selectedCurrency = localStorage.getItem('selectedCurrency');
    translate.setDefaultLang('en');
    menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    })
    currencyService.setCurrency("EUR");
  }

  ngOnInit(): void {
        this.router.events.subscribe(event => {
          if (event instanceof NavigationEnd) {
            const currentUrl = event.urlAfterRedirects;
            this.showSidemenu = !currentUrl.includes('/login');
          }
        })
    }

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

  async export() {
    this.exportService.getExport().subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = 'export.xlsx';
        a.click();

        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Error exporting', error);
      }
    });
  }
}
