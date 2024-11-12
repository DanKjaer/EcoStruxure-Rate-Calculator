import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Currency} from '../models';
import {firstValueFrom} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CurrencyService {
  private apiUrl = 'http://localhost:8080/api/currency';
  constructor(private http: HttpClient) {}

  getCurrencies(): Promise<Currency[]> {
    return firstValueFrom(this.http.get<Currency[]>(`${this.apiUrl}`));
  }
}
