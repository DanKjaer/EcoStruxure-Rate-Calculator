import { Injectable } from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';

@Injectable({
  providedIn: 'root'
})
export class SearchConfigService {

  constructor() { }

  configureFilter<T extends Record<string, any>>(
    dataSource: MatTableDataSource<T>,
    nestedPropertyKeys: string[] = []
  ): void {
    dataSource.filterPredicate = (data: T, filter: string) => {
      const filterValue = filter.trim().toLowerCase();

      // Resolve nested properties
      const nestedProperties = nestedPropertyKeys
        .map(key => this.resolveNestedProperty(data, key)?.toString().toLowerCase() || '')
        .join(' ');

      // Combine flat properties
      const flatProperties = Object.keys(data)
        .map(key => {
          const value = data[key];
          return value !== null && value !== undefined ? value.toString().toLowerCase() : '';
        })
        .join(' ');

      // Return true if any property matches the filter
      return nestedProperties.includes(filterValue) || flatProperties.includes(filterValue);
    };
  }

  private resolveNestedProperty(obj: any, path: string): any {
    return path.split('.').reduce((prev, curr) => prev?.[curr], obj);
  }
}
