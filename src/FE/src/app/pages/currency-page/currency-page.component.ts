import {AfterViewInit, Component, OnInit} from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import {MatTableModule, MatTableDataSource} from '@angular/material/table';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {NgClass, NgIf} from '@angular/common';
import {MenuService} from '../../services/menu.service';
import {CurrencyService} from '../../services/currency.service';

@Component({
  selector: 'app-currency-page',
  standalone: true,
  imports: [
    TranslateModule,
    MatTableModule,
    MatProgressSpinner,
    NgIf,
    NgClass,
  ],
  templateUrl: './currency-page.component.html',
  styleUrl: './currency-page.component.css'
})
export class CurrencyPageComponent implements AfterViewInit, OnInit {

  constructor(private menuService: MenuService, private currencyService: CurrencyService) {
  }

  isMenuOpen: boolean | undefined;

  displayedColumns: string[] = [
    'currency',
    'eur conversion rate',
    'usd conversion rate',
    'symbol'
  ];

  datasource = new MatTableDataSource();
  loading = true;

  ngOnInit() {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    });
  }

  async ngAfterViewInit() {
    this.datasource.data = await this.currencyService.getCurrencies();
    this.loading = false;
  }
}
