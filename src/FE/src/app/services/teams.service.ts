import { Injectable } from '@angular/core';
import {firstValueFrom} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Team, TeamDTO, TeamProfile} from "../models";

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
   * Gets a team by id.
   * @param id
   */
  getTeam(id: string): Promise<Team> {
    return firstValueFrom(this.http.get<Team>(`${this.apiUrl}?teamId=${id}`));
  }

  /**
   * Gets team profiles by team id.
   * @param id
   */
  getTeamProfiles(id: string): Promise<TeamProfile[]> {
    return firstValueFrom(this.http.get<TeamProfile[]>(`${this.apiUrl}/profiles?profileId=${id}`));
  }

  /**
   * Creates a new team.
   * @param team
   * @param teamProfiles
   */
  postTeam(team: Team): Promise<Team> {
    return firstValueFrom(this.http.post<Team>(`${this.apiUrl}`, team));
  }

  /**
   * Updates a team by id.
   * @param team
   */
  putTeam(team: TeamDTO): Promise<Team> {
    return firstValueFrom(this.http.put<Team>(`${this.apiUrl}`, team));
  }

  /**
   * Deletes a team by id.
   * @param teamID
   */
  deleteTeam(teamID: string): Promise<boolean> {
    return firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}/${teamID}`));
  }

  /**
   * Deletes a teamProfile by id from a team.
   * @param teamProfile
   */
  deleteTeamProfile(teamProfileId: string): Promise<boolean> {
    return firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}/teamProfile?teamProfileId=${teamProfileId}`));
  }

  addProfileToTeams(newTeamProfiles: TeamProfile[]) {
    return firstValueFrom(this.http.put<TeamProfile[]>(`${this.apiUrl}/addProfileToTeams`, newTeamProfiles));
  }
}
