import {AfterViewInit, Component, OnInit} from '@angular/core';
import {DecimalPipe, NgClass, NgIf} from '@angular/common';
import {MatButton, MatIconButton} from '@angular/material/button';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable, MatTableDataSource
} from '@angular/material/table';
import {MatFormField, MatLabel, MatPrefix, MatSuffix} from '@angular/material/form-field';
import {MatIcon} from '@angular/material/icon';
import {MatInput} from '@angular/material/input';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatOption} from '@angular/material/core';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatRadioButton, MatRadioGroup} from '@angular/material/radio';
import {MatSelect} from '@angular/material/select';
import {ReactiveFormsModule} from '@angular/forms';
import {TranslateModule} from '@ngx-translate/core';
import {MatList} from '@angular/material/list';
import {CurrencyService} from '../../services/currency.service';

@Component({
  selector: 'app-world-overview-page',
  standalone: true,
  imports: [
    NgClass,
    DecimalPipe,
    MatButton,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatFormField,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatInput,
    MatLabel,
    MatPrefix,
    MatProgressSpinner,
    MatRow,
    MatRowDef,
    MatTable,
    NgIf,
    ReactiveFormsModule,
    TranslateModule,
    MatHeaderCellDef,
  ],
  templateUrl: './world-overview-page.component.html',
  styleUrl: './world-overview-page.component.css'
})
export class WorldOverviewPageComponent implements OnInit, AfterViewInit {
  isMenuOpen: boolean | undefined;
  datasource: MatTableDataSource<any> = new MatTableDataSource();
  displayedColumns: string[] = [
    'name',
    'dayRate',
    'totalGrossMargin',
    'totalPrice'
  ];

  protected readonly localStorage = localStorage;
  loading: boolean = true;

  constructor(protected currencyService: CurrencyService) { }

  ngOnInit(): void {
        this.datasource.data = this.data;
        this.loading = false;
    }

  ngAfterViewInit(): void {

    }

  // MOCK DATA
  data = [
    {
      name: 'Denmark',
      dayRate: 1000,
      totalGrossMargin: 66.66,
      totalPrice: 1500
    },
    {
      name: 'Russia',
      dayRate: 2000,
      totalGrossMargin: 66.66,
      totalPrice: 3000
    },
    {
      name: 'China',
      dayRate: 3000,
      totalGrossMargin: 66.66,
      totalPrice: 4500
    }
  ];
}
