import {Component, Input, OnChanges, SimpleChange, SimpleChanges} from '@angular/core';
import {DashboardCountry} from '../../../models';
import {NgxEchartsDirective} from 'ngx-echarts';
import {TranslateService} from '@ngx-translate/core';

@Component({
    selector: 'app-country-projects-graph',
    standalone: true,
    imports: [
        NgxEchartsDirective
    ],
    templateUrl: './country-projects-graph.component.html',
    styleUrl: './country-projects-graph.component.css'
})
export class CountryProjectsGraphComponent implements OnChanges {
    @Input() data?: DashboardCountry[];
    chartOptions: any;


    constructor(private translateService: TranslateService) {
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['data'] && this.data) {
            this.updateChart();
        }
    }

    updateChart(): void {
        this.chartOptions = {
            title: {
                text: this.translateService.instant('PROJECTS_IN_COUNTRY'),
                subtext: 'Total: ' + this.data?.reduce((acc, country) => acc + country.projects.length, 0),
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
                    show: false
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
