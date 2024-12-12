import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {NgxEchartsDirective} from 'ngx-echarts';
import {DashboardProject} from '../../../models';

@Component({
  selector: 'app-projects-graph',
  standalone: true,
  imports: [
    NgxEchartsDirective
  ],
  templateUrl: './projects-graph.component.html',
  styleUrls: ['./projects-graph.component.css']
})
export class ProjectsGraphComponent implements OnChanges {
  @Input() data?: DashboardProject[];
  @Input() country?: string;
  chartOptions: any;

  ngOnChanges(change: SimpleChanges) {
    if (change['data'] && this.data) {
      this.updateChart();
    }
  }

  updateChart(): void {
    const dynamicWidth = ((this.data?.length || 1) * 100) + 300;
    //Check for multiple projects to show average line
    const hasMultipleValues = this.data && this.data
      .map(project => project.price)
      .filter(price => price !== null).length > 1;

    this.chartOptions = {
      title: {
        text: `Projects in ${this.country}`,
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      xAxis: {
        type: 'category',
        data: this.data?.map(project => project.name)
      },
      yAxis: {
        type: 'value',
        nameTextStyle: {
          align: 'right',
          fontSize: 8,
        },
        axisLabel: {
          formatter: (value: number) => {
            if (value >= 1000000 || value <= -1000000) {
              return (value / 1000000).toFixed(1) + 'M';
            } else if (value > 1000 || value <= -1000) {
              return (value / 1000).toFixed(1) + 'K';
            } else {
              return value.toString();
            }
          }
        }
      },
      toolbox: {
        show: true,
        orient: 'vertical',
        itemSize: 20,
        feature: {
          saveAsImage: {}
        }
      },
      series: [
        {
          name: 'Project price',
          type: 'bar',
          stack: 'a',
          data: this.data?.map(project => project.price),
          markLine: hasMultipleValues ? {
            lineStyle: {
              type: [5, 8],
              color: 'green',
              width: 2
            },
            label: {
              fontSize: 10,
              formatter: (params: any) => {
                return new Intl.NumberFormat('en-US').format(Math.round(params.value));
              }
            },
            symbol: ['none', 'none'],
            data: [{ type: 'average' }]
          }
          : undefined,
        }
      ]
    };
    const chartContainer = document.querySelector('.projectGraphContainer') as HTMLElement;
    if (chartContainer && dynamicWidth) {
      chartContainer.style.width = `${dynamicWidth}px`
    }
  }

}
