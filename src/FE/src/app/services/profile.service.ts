import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom, Observable} from 'rxjs';
import {Profile} from '../models';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  private apiUrl = 'http://localhost:8080/api';
  constructor(private http: HttpClient) { }

  getProfiles(): Promise<Profile[]> {
    return firstValueFrom(this.http.get<Profile[]>(`${this.apiUrl}/profile`));
  }

  postProfile(): Promise<Profile> {
    return firstValueFrom(this.http.post<Profile>(`${this.apiUrl}/profile`, {}));
  }

  putProfile(): Promise<boolean> {
    return firstValueFrom(this.http.put<boolean>(`${this.apiUrl}/profile/{id}`, {}));
  }

  deleteProfile(): Promise<boolean> {
    return firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}/profile/{id}`));
  }

}
