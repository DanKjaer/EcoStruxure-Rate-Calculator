import {AfterViewInit, Component, OnInit} from '@angular/core';
import {DecimalPipe, NgClass, NgIf} from '@angular/common';
import {MatIconButton} from '@angular/material/button';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatFormField, MatLabel, MatPrefix} from '@angular/material/form-field';
import {MatIcon} from '@angular/material/icon';
import {MatInput} from '@angular/material/input';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {ReactiveFormsModule} from '@angular/forms';
import {TranslateModule} from '@ngx-translate/core';
import {CurrencyService} from '../../services/currency.service';
import {MenuService} from '../../services/menu.service';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatTooltip} from '@angular/material/tooltip';
import {MatTreeModule} from '@angular/material/tree';
import {MatDivider} from '@angular/material/divider';
import {
  MatList,
  MatListItem,
  MatListItemIcon, MatListItemLine,
  MatListItemTitle,
  MatListSubheaderCssMatStyler
} from '@angular/material/list';

// mock model
export interface countryData {
  name: string;
  dayRate: number;
  totalGrossMargin: number;
  totalPrice: number;
}

interface treeNode {
  name: string;
  children?: treeNode[];
}

interface listItem {
  name: string;
  cost: number;
  grossMargin: number;
}

@Component({
  selector: 'app-world-overview-page',
  standalone: true,
  imports: [
    NgClass,
    DecimalPipe,
    MatTableModule,
    MatFormField,
    MatIcon,
    MatInput,
    MatLabel,
    MatPrefix,
    MatProgressSpinner,
    NgIf,
    ReactiveFormsModule,
    TranslateModule,
    MatIconButton,
    MatExpansionModule,
    MatTooltip,
    MatTreeModule,
    MatDivider,
    MatList,
    MatListSubheaderCssMatStyler,
    MatListItem,
    MatListItemTitle,
    MatListItemLine
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
  countrySelected: boolean = false;
  selectedCountry: countryData | null = null;

  childrenAccessor = (node: treeNode) => node.children ?? []
  hasChild = (_: number, node: any) => !!node.children && node.children.length > 0;

  constructor(protected currencyService: CurrencyService,
              private menuService: MenuService) {
  }

  ngOnInit(): void {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    });
    this.datasource.data = this.data;
    this.loading = false;
  }

  ngAfterViewInit(): void {

  }

  applySearch(event: Event) {
    const searchValue = (event.target as HTMLInputElement).value;
    this.datasource.filter = searchValue.trim().toLowerCase();

    if (this.datasource.paginator) {
      this.datasource.paginator.firstPage();
    }
  }

  selectCountry(row: countryData) {
    this.selectedCountry = row;
    this.countrySelected = true;
  }

  clearSelection() {
    this.countrySelected = false;
    this.selectedCountry = null;
  }

  // MOCK DATA

  data: countryData[] = [
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

  treeData: treeNode[] = [
    {
      name: 'Projects',
      children: [{name: 'Project 1'}, {name: 'Project 2'}]
    }, {
      name: 'Teams',
      children: [{name: 'Team 1'}, {name: 'Team 2'}, {name: 'Team 3'}]
    }
  ];

  infoItems: listItem[] = [
    {
      name: 'Project 1',
      cost: 1000,
      grossMargin: 1500
    },
    {
      name: 'Project 2',
      cost: 3000,
      grossMargin: 6000
    }
  ];
}
