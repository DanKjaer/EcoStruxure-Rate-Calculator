import {AfterViewInit, Component, OnInit} from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import {MatTableModule, MatTableDataSource} from '@angular/material/table';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatIcon} from '@angular/material/icon';
import {MatCard, MatCardContent} from '@angular/material/card';
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
    MatIcon,
    MatCard,
    MatCardContent,
    NgClass
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

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if (file.type === 'text/csv') {
        const reader = new FileReader();
        reader.onload = (e: ProgressEvent<FileReader>) => {
          /**
           * do something with data here - e.g. make sure its clean and send it to the server
           */
          const csvData = e.target?.result;
          console.log(csvData);
        };
        reader.readAsText(file);
      } else {
        alert('Please upload a valid CSV file.');
      }
    }
  }
}
