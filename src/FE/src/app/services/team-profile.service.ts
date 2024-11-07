import { Injectable } from '@angular/core';
import {TeamDTO} from '../models';
import {firstValueFrom} from 'rxjs';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TeamProfileService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8080/api/team-profile';
  
  getTeamsFromProfile(id: string): Promise<TeamDTO> {
    return firstValueFrom(this.http.get<TeamDTO>(`${this.apiUrl}?teamId=${id}`));
  }

}
