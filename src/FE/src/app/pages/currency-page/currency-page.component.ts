import {AfterViewInit, Component, OnInit} from '@angular/core';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {MatTableModule, MatTableDataSource} from '@angular/material/table';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {NgClass, NgIf} from '@angular/common';
import {MenuService} from '../../services/menu.service';
import {CurrencyService} from '../../services/currency.service';
import {Currency} from '../../models';
import {SnackbarService} from '../../services/snackbar.service';

@Component({
  selector: 'app-currency-page',
  standalone: true,
  imports: [
    TranslateModule,
    MatTableModule,
    MatProgressSpinner,
    NgIf,
    NgClass
  ],
  templateUrl: './currency-page.component.html',
  styleUrl: './currency-page.component.css'
})
export class CurrencyPageComponent implements AfterViewInit, OnInit {

  constructor(private menuService: MenuService,
              private currencyService: CurrencyService,
              private snackbarService: SnackbarService,
              private translateService: TranslateService) {
  }

  isMenuOpen: boolean | undefined;

  displayedColumns: string[] = [
    'currency',
    'eur conversion rate',
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

  async onFileSelected(event: Event): Promise<void> {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      if (file.type === 'text/csv' || file.type === 'application/vnd.ms-excel') {
        const reader = new FileReader();
        reader.readAsText(file);

        reader.onload = async (e: ProgressEvent<FileReader>) => {
          const csvData = e.target?.result;
          if (typeof csvData === "string") {

            const data = this.parseCsv(csvData);
            await this.currencyService.importCurrency(data);
            this.datasource.data = data!;
            this.snackbarService.openSnackBar(this.translateService.instant("SUCCESS_UPDATING_CURRENCY"), true);
          }
        };

      } else {
        alert('Please upload a valid CSV file.');
      }
    }
  }

  parseCsv(csv: string): Currency[] {
    const rows = csv.split('\n');
    const headers = rows[0].split(';');
    return rows.slice(1).map(row => {
      const values = row.split(';');
      return {
        currencyCode: values[0].trim() || '',
        eurConversionRate: values[1] ? parseFloat(values[1]) : 0,
        symbol: values[2].trim() || ''
      } as Currency;
    })
  }
}
