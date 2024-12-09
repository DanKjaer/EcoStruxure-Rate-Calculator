import {Component} from '@angular/core';
import {NgxEchartsDirective} from 'ngx-echarts';
import {EChartsCoreOption} from 'echarts';

@Component({
  selector: 'app-projects-graph',
  standalone: true,
  imports: [
    NgxEchartsDirective
  ],
  templateUrl: './projects-graph.component.html',
  styleUrls: ['./projects-graph.component.css']
})
export class ProjectsGraphComponent {
  chartOptions: any = {
    title: {
      text: 'Project Chart'
    },
    tooltip: {},
    xAxis: {
      data: ['Project1', 'Project2', 'Project3']
    },
    yAxis: {},
    series: [
      {
        name: 'Project Cost',
        type: 'bar',
        stack: 'a',
        data: [5, 20, 36],
        itemStyle: {
          color: '#e63535'
        }
      },
      {
        name: 'Project GM',
        type: 'bar',
        stack: 'a',
        data: [20, 12, 33],
        itemStyle: {
          color: '#36bd2f',
          borderRadius: [20, 20, 0, 0]
        }
      }
    ]
  };

  constructor() {
  }
}
