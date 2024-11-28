import { Injectable } from '@angular/core';
import {firstValueFrom} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Team, TeamProfiles} from "../models";

@Injectable({
  providedIn: 'root'
})
export class TeamsService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8080/api/teams';

  /**
   * Gets all teams.
   */
  getTeams(): Promise<Team[]> {
    return firstValueFrom(this.http.get<Team[]>(`${this.apiUrl}/all`));
  }

  /**
   * Gets team profiles by team id.
   * @param id
   */
  getTeamProfiles(id: string): Promise<TeamProfiles[]> {
    return firstValueFrom(this.http.get<TeamProfiles[]>(`${this.apiUrl}/profiles?profileId=${id}`));
  }

  /**
   * Creates a new team.
   * @param team
   * @param teamProfiles
   */
  postTeam(team: Team, teamProfiles: TeamProfiles[]): Promise<Team> {
    return firstValueFrom(this.http.post<Team>(`${this.apiUrl}`, {team, teamProfiles}));
  }

  /**
   * Updates a team by id.
   * @param team
   */
  putTeam(team: Team): Promise<Team> {
    return firstValueFrom(this.http.put<Team>(`${this.apiUrl}?teamId=${team.teamId}`, team));
  }

  /**
   * Deletes a team by id.
   * @param teamID
   */
  deleteTeam(teamID: string): Promise<boolean> {
    return firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}/${teamID}`));
  }
}
