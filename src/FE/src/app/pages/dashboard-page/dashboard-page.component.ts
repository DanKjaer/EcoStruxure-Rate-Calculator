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
  MatListItemLine,
  MatListItemTitle,
  MatListSubheaderCssMatStyler
} from '@angular/material/list';
import {ProjectsGraphComponent} from '../../components/graphs/projects-graph/projects-graph.component';
import {DashboardCountry, DashboardProject, TreeNode} from '../../models';
import {DashboardService} from '../../services/dashboard.service';
import {
  CountryProjectsGraphComponent
} from '../../components/graphs/country-projects-graph/country-projects-graph.component';

@Component({
  selector: 'app-dashboard-page',
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
    MatListItemLine,
    ProjectsGraphComponent,
    CountryProjectsGraphComponent
  ],
  templateUrl: './dashboard-page.component.html',
  styleUrl: './dashboard-page.component.css'
})

export class DashboardPageComponent implements OnInit, AfterViewInit {
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
  selectedCountry: DashboardCountry | null = null;
  countryData: DashboardCountry[] = [];
  selectedCountryProjects: DashboardProject[] = [];
  treeData: TreeNode[] = [];

  childrenAccessor = (node: TreeNode) => node.children ?? []
  hasChild = (_: number, node: any) => !!node.children && node.children.length > 0;

  constructor(protected currencyService: CurrencyService,
              private menuService: MenuService,
              private dashboardService: DashboardService) {
  }

  async ngOnInit(): Promise<void> {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    });
    this.countryData = await this.dashboardService.getDashboard();
    console.log(this.countryData);
    this.datasource.data = this.countryData;
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

  selectCountry(row: DashboardCountry) {
    this.selectedCountry = row;
    this.countrySelected = true;
    this.fillCountryData();
    this.selectedCountryProjects = this.selectedCountry!.projects;
  }

  clearSelection() {
    const container = document.querySelector('.selectedCountryContainer');
    container!.classList.add('closing');

    setTimeout(() => {
      this.countrySelected = false;
      this.selectedCountry = null;
      container!.classList.remove('closing');
    }, 100);
  }

  fillCountryData() {
    this.treeData = [
      {
        name: 'Projects',
        children: this.selectedCountry!.projects.map(project => ({
          name: `${project.name}`
        }))
      },
      {
        name: 'Teams'
      }
    ];
  }
}
