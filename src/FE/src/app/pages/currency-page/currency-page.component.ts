import {AfterViewInit, Component} from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import {MatTableModule, MatTableDataSource} from '@angular/material/table';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {NgIf} from '@angular/common';
import {MatIcon} from '@angular/material/icon';
import {MatCard, MatCardContent} from '@angular/material/card';

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
    }, 500)
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
