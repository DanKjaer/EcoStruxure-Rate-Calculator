import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TeamProfileService {

  constructor() { }

  private apiUrl = 'http://localhost:8080/api/team-profile';
}
