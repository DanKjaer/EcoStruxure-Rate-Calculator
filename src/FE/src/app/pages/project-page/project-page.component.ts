import {Component, OnInit} from '@angular/core';
import {DecimalPipe, NgClass, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable, MatTableDataSource
} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {MatFormField, MatInput} from "@angular/material/input";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {MatLabel} from "@angular/material/form-field";
import {Project} from "../../models";
import {ProjectService} from '../../services/project.service';
import {ActivatedRoute} from '@angular/router';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';
import {MenuService} from '../../services/menu.service';
import {SnackbarService} from '../../services/snackbar.service';

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
    MatProgressSpinner,
    MatRow,
    MatRowDef,
    MatTable,
    NgIf,
    TranslateModule,
    NgClass,
    MatFormField,
    MatMenuTrigger,
    ReactiveFormsModule,
    MatHeaderCellDef,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatDatepicker
  ],
  templateUrl: './project-page.component.html',
  styleUrl: './project-page.component.css'
})
export class ProjectPageComponent implements OnInit{
    projectForm: FormGroup = new FormGroup({});
    project!: Project;

  statBoxes = {
    totalDayRate: 0,
    totalPrice: 0,
    grossMargin: 0,
    totalDays: 0
  };
  loading: boolean = true;
  isMenuOpen: boolean | undefined;
  datasource: MatTableDataSource<any> = new MatTableDataSource();
  displayedColumns: string[] = [
    'projectName',
    'projectAllocation',
    'dayRate',
    'options'
  ];


  constructor(private formBuilder: FormBuilder,
              private projectService: ProjectService,
              private route: ActivatedRoute,
              private menuService: MenuService,
              private snackBar: SnackbarService,
              private translate: TranslateService) {
  }

  async ngOnInit(): Promise<void> {
    this.menuService.isMenuOpen$.subscribe((isOpen) => {
      this.isMenuOpen = isOpen;
    });

    this.projectForm = this.formBuilder.group({
        projectName: ['', Validators.required],
        salesNumber: [''],
        projectPrice: [''],
        projectDescription: [''],
        startDate: [''],
        endDate: ['']
    });

    this.project = await this.projectService.getProject(this.route.snapshot.paramMap.get('id')!);
    this.project.projectMembers.forEach(member => {
      member.dayRateWithMarkup = member.dayRate! * (member.markup! / 100 + 1);
    });
    this.datasource.data = this.project.projectMembers;
    this.loading = false;

    this.statBoxes = {
      totalDayRate: this.project.projectDayRate!,
      totalPrice: this.project.projectPrice!,
      grossMargin: this.project.projectGrossMargin!,
      totalDays: this.project.projectTotalDays!
    }
  }


  async update() {
    if(this.projectForm.valid) {
      let updatedProject = {
        projectId: this.project.projectId,
        projectName: this.projectForm.value.projectName,
        projectSalesNumber: this.projectForm.value.salesNumber,
        projectPrice: this.projectForm.value.projectPrice,
        projectDescription: this.projectForm.value.projectDescription,
        projectStartDate: this.projectForm.value.startDate,
        projectEndDate: this.projectForm.value.endDate,
        projectLocation: this.project.projectLocation,
        projectMembers: this.project.projectMembers
      };

      const response = await this.projectService.putProject(updatedProject);
      if (response != undefined) {
        this.snackBar.openSnackBar(this.translate.instant('SUCCESS_PROJECT_UPDATED'), true);
      } else {
        this.snackBar.openSnackBar(this.translate.instant('ERROR_PROJECT_UPDATED'), false);
      }
    }
  }

  undo() {

  }
}
