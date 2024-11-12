import {AfterViewInit, Component, OnInit} from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import {MatTableModule, MatTableDataSource} from '@angular/material/table';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {NgClass, NgIf} from '@angular/common';
import {MenuService} from '../../services/menu.service';

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

  constructor(private menuService: MenuService) {
  }

  isMenuOpen: boolean | undefined;

  displayedColumns: string[] = [
    'currency',
    'eur conversion rate',
    'usd conversion rate'
  ];

  datasource = new MatTableDataSource([{}]);
  loading = true;

  ngOnInit() {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    });
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.datasource.data = [
        {
          currency: 'EUR',
          'eur conversion rate': 1.00,
          'usd conversion rate': 0.93
        },
        {
          currency: 'USD',
          'eur conversion rate': 1.08,
          'usd conversion rate': 1.00
        }
      ];
      this.loading = false;
    }, 2000)
  }
}
