import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {firstValueFrom, Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class ExportService {

  private apiUrl = 'http://localhost:8080/api/excel';

  constructor(private http: HttpClient) {
  }

  getExport(): Observable<Blob> {
    return this.http.get(this.apiUrl, {responseType: 'blob'});
  }
}
