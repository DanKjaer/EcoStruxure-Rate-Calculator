import {Component, OnInit} from '@angular/core';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatFormField, MatLabel, MatPrefix} from '@angular/material/form-field';
import {MatIcon} from '@angular/material/icon';
import {MatInput} from '@angular/material/input';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatOption} from '@angular/material/core';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatRadioButton, MatRadioGroup} from '@angular/material/radio';
import {MatSelect} from '@angular/material/select';
import {DecimalPipe, NgClass, NgIf} from '@angular/common';
import {ReactiveFormsModule} from '@angular/forms';
import {TranslateModule} from '@ngx-translate/core';
import {TeamProfileService} from '../../services/team-profile.service';
import {TeamDTO, TeamProfiles} from '../../models';
import {ActivatedRoute} from '@angular/router';
import {MenuService} from '../../services/menu.service';
import {CurrencyService} from '../../services/currency.service';

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
    MatOption,
    MatPrefix,
    MatProgressSpinner,
    MatRadioButton,
    MatRadioGroup,
    MatSelect,
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

  constructor(private teamProfileService: TeamProfileService,
              private route: ActivatedRoute,
              private menuService: MenuService,
              protected currencyService: CurrencyService) {
  }

  teamInfo: TeamDTO | undefined;
  profiles: TeamProfiles[] = [];
  isMenuOpen: boolean | undefined;

  statBoxes = {
    rawHourlyRate: 0,
    markupHourlyRate: 0,
    gmHourlyRate: 0,
    totalAnnualHours: 0
  }
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
    this.teamInfo = await this.teamProfileService.getTeamsFromProfile(this.route.snapshot.paramMap.get('id')!);
    this.profiles = this.teamInfo.teamProfiles;
    this.datasource.data = this.profiles;
    let markupHourlyRate = this.teamInfo.team.hourlyRate! * ((this.teamInfo.team.markup! / 100) + 1);
    let gmHourlyRate = markupHourlyRate * ((this.teamInfo.team.grossMargin! / 100) + 1);
    this.statBoxes = {
      rawHourlyRate: this.teamInfo.team.hourlyRate!,
      markupHourlyRate: markupHourlyRate,
      gmHourlyRate: gmHourlyRate,
      totalAnnualHours: this.profiles.reduce((sum, item) => sum + item.annualHours!, 0)
    }
    this.loading = false;
  }

  protected readonly localStorage = localStorage;
}
