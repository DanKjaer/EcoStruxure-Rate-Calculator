import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom} from 'rxjs';
import {Geography} from '../models';

@Injectable({
  providedIn: 'root'
})
export class GeographyService {
  private apiUrl = 'http://localhost:8080/api/geography';
  constructor(private http : HttpClient) { }

  getCountries(): Promise<Geography[]> {
    return firstValueFrom(this.http.get<Geography[]>(`${this.apiUrl}`));
  }
}
