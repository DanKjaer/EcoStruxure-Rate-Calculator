import { Injectable } from '@angular/core';
import {firstValueFrom, Observable} from 'rxjs';
import {Team} from '../models';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TeamsService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8080/api/teams';

  getTeams(): Promise<Team[]> {
    return firstValueFrom(this.http.get<Team[]>(`${this.apiUrl}`));
  }

  postTeam(): Promise<Team> {
    return firstValueFrom(this.http.post<Team>(`${this.apiUrl}`, {}));
  }

  putTeam(): Promise<boolean> {
    return firstValueFrom(this.http.put<boolean>(`${this.apiUrl}/{id}`, {}));
  }

  deleteTeam(): Promise<boolean> {
    return firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}/{id}`));
  }
}
