import {Component, Input, OnChanges, SimpleChange, SimpleChanges} from '@angular/core';
import {DashboardCountry} from '../../../models';
import {NgxEchartsDirective} from 'ngx-echarts';

@Component({
  selector: 'app-country-projects-graph',
  imports: [
    NgxEchartsDirective
  ],
  templateUrl: './country-projects-graph.component.html',
  styleUrl: './country-projects-graph.component.css'
})
export class CountryProjectsGraphComponent implements OnChanges{
  @Input() data?: DashboardCountry[];
  chartOptions: any;

  ngOnChanges(changes: SimpleChanges) {
    if (changes['data'] && this.data) {
      this.updateChart();
    }
  }

  updateChart(): void {
    this.chartOptions = {
      title: {
        text: 'Projects per country'
      },
      tooltip: {
        trigger: 'item',
      },
      series: {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '20',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: this.data?.map(country => ({
          value: country.projects.length,
          name: country.name
        }))
      },
    }
  }
}
