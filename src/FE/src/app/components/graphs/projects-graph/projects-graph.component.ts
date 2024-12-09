import {Component} from '@angular/core';
import { NgApexchartsModule } from "ng-apexcharts";
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-projects-graph',
  standalone: true,
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    NgApexchartsModule,
  ],
  templateUrl: './projects-graph.component.html',
  styleUrls: ['./projects-graph.component.css']
})
export class ProjectsGraphComponent {

}
