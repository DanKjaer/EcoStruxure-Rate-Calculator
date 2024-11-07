import {AfterViewInit, Component} from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import {MatTableModule, MatTableDataSource} from '@angular/material/table';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-currency-page',
  standalone: true,
  imports: [
    TranslateModule,
    MatTableModule,
    MatProgressSpinner,
    NgIf,
  ],
  templateUrl: './currency-page.component.html',
  styleUrl: './currency-page.component.css'
})
export class CurrencyPageComponent implements AfterViewInit {
  displayedColumns: string[] = [
    'currency',
    'eur conversion rate',
    'usd conversion rate'
  ];

  datasource = new MatTableDataSource([{}]);
  loading = true;

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
