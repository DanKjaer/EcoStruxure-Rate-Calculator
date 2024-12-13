import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom} from 'rxjs';
import {DashboardCountry} from '../models';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/api/dashboard';
  constructor(private http: HttpClient) { }

  /**
   * Gets dashboard data
   */
  getDashboard():Promise<DashboardCountry[]> {
    return firstValueFrom(this.http.get<DashboardCountry[]>(`${this.apiUrl}`));
  }
}
