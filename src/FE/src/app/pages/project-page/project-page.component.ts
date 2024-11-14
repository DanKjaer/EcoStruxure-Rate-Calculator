import {Component, OnInit} from '@angular/core';
import {DecimalPipe, NgClass, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable, MatTableDataSource
} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {MatFormField, MatInput} from "@angular/material/input";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatPaginator} from "@angular/material/paginator";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatSort, MatSortHeader} from "@angular/material/sort";
import {MatTooltip} from "@angular/material/tooltip";
import {TranslateModule} from "@ngx-translate/core";
import { MatSelect} from "@angular/material/select";
import {MatOption} from "@angular/material/core";
import {MatLabel} from "@angular/material/form-field";
import {TeamsService} from "../../services/teams.service";
import {Project, Team} from "../../models";
import {ProjectService} from '../../services/project.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-project-page',
  standalone: true,
  imports: [
    DecimalPipe,
    FormsModule,
    MatButton,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatMenu,
    MatMenuItem,
    MatPaginator,
    MatProgressSpinner,
    MatRow,
    MatRowDef,
    MatSort,
    MatSortHeader,
    MatTable,
    MatTooltip,
    NgIf,
    TranslateModule,
    NgClass,
    MatFormField,
    MatMenuTrigger,
    ReactiveFormsModule,
    MatSelect,
    MatOption
  ],
  templateUrl: './project-page.component.html',
  styleUrl: './project-page.component.css'
})
export class ProjectPageComponent implements OnInit{
    projectForm: FormGroup = new FormGroup({});
    project!: Project;

  statBoxes = {
    totalDayRate: '',
    totalHourlyRate: '',
    totalAnnualCost: '',
    totalAnnualHours: ''
  };
  loading: boolean = true;
  isMenuOpen: boolean | undefined;
  datasource: MatTableDataSource<any> = new MatTableDataSource();
  displayedColumns: string[] = [
    'teamName',
    'projectAllocation',
    'dayRate',
    'options'
  ];


  constructor(private formBuilder: FormBuilder,
              private projectService: ProjectService,
              private route: ActivatedRoute) {
  }

  async ngOnInit(): Promise<void> {
    this.projectForm = this.formBuilder.group({
        teamName: ['', Validators.required],
        projectDescription: [''],
        projectAllocation: [''],
        dayRateOnProject: ['']
    });

    this.project = await this.projectService.getProject(this.route.snapshot.paramMap.get('id')!);
    this.datasource.data = this.project.projectMembers;
  }


  update() {

  }

  undo() {

  }
}
