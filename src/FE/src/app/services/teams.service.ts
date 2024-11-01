import { Injectable } from '@angular/core';
import {firstValueFrom, Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Team, TeamProfiles} from "../models";

@Injectable({
  providedIn: 'root'
})
export class TeamsService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8080/api/teams';

  getTeams(): Promise<Team[]> {
    return firstValueFrom(this.http.get<Team[]>(`${this.apiUrl}`));
  }

  postTeam(team: Team, teamProfiles: TeamProfiles[]): Promise<Team> {
    return firstValueFrom(this.http.post<Team>(`${this.apiUrl}`, {team, teamProfiles}));
  }

  putTeam(): Promise<boolean> {
    return firstValueFrom(this.http.put<boolean>(`${this.apiUrl}/{id}`, {}));
  }

  deleteTeam(teamID: string): Promise<boolean> {
    return firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}/${teamID}`));
  }
}
