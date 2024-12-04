import {Component, OnInit} from '@angular/core';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatFormField, MatLabel, MatPrefix} from '@angular/material/form-field';
import {MatIcon} from '@angular/material/icon';
import {MatInput} from '@angular/material/input';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {DecimalPipe, NgClass, NgIf} from '@angular/common';
import {ReactiveFormsModule} from '@angular/forms';
import {TranslateModule} from '@ngx-translate/core';
import {Team, TeamProfile} from '../../models';
import {ActivatedRoute} from '@angular/router';
import {MenuService} from '../../services/menu.service';
import {CurrencyService} from '../../services/currency.service';
import {TeamsService} from '../../services/teams.service';

@Component({
  selector: 'app-team-page',
  standalone: true,
  imports: [
    MatButton,
    MatTableModule,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatMenu,
    MatMenuItem,
    MatPrefix,
    MatProgressSpinner,
    NgIf,
    ReactiveFormsModule,
    TranslateModule,
    MatMenuTrigger,
    NgClass,
    DecimalPipe
  ],
  templateUrl: './team-page.component.html',
  styleUrl: './team-page.component.css'
})
export class TeamPageComponent implements OnInit {

  constructor(private teamService: TeamsService,
              private route: ActivatedRoute,
              private menuService: MenuService,
              protected currencyService: CurrencyService) {
  }

  team: Team | undefined;
  teamProfiles: TeamProfile[] = [];
  isMenuOpen: boolean | undefined;

  statBoxes = {
    rawHourlyRate: 0,
    markupHourlyRate: 0,
    gmHourlyRate: 0,
    totalAnnualHours: 0
  }
  protected readonly localStorage = localStorage;
  loading: boolean = true;
  datasource: MatTableDataSource<any> = new MatTableDataSource();
  displayedColumns: string[] = [
    'name',
    'hours allocation',
    'annual hours allocated',
    'cost allocation',
    'cost on team',
    'location',
    'day rate',
    'options'
  ]

  async ngOnInit() {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    });
    this.team = await this.teamService.getTeam(this.route.snapshot.paramMap.get('id')!);
    this.teamProfiles = this.team.teamProfiles!;
    this.datasource.data = this.teamProfiles;
    let markupHourlyRate = this.team.hourlyRate! * ((this.team.markupPercentage! / 100) + 1);
    let gmHourlyRate = markupHourlyRate * ((this.team.grossMarginPercentage! / 100) + 1);
    this.statBoxes = {
      rawHourlyRate: this.team.hourlyRate!,
      markupHourlyRate: markupHourlyRate,
      gmHourlyRate: gmHourlyRate,
      totalAnnualHours: this.teamProfiles.reduce((sum, item) => sum + item.allocatedHours!, 0)
    }
    this.loading = false;
    console.log("team: ", this.team);
    console.log("teamProfiles: ", this.teamProfiles);
  }

  applySearch(event: Event) {
    const searchValue = (event.target as HTMLInputElement).value;
    this.datasource.filter = searchValue.trim().toLowerCase();

    if (this.datasource.paginator) {
      this.datasource.paginator.firstPage();
    }
  }

  calculateDayRate(teamProfile: TeamProfile) {
    if(teamProfile.allocatedHours === 0) {
      return 0;
    }
    let hourlyRate = teamProfile.allocatedCost! / teamProfile.allocatedHours!;
    return hourlyRate * 8;
  }
}
