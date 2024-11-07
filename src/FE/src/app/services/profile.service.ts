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

  //Gets a list of profiles
  getProfiles(): Promise<Profile[]> {
    return firstValueFrom(this.http.get<Profile[]>(`${this.apiUrl}/profile`));
  }
  //Creates a profile
  postProfile(profile: Profile): Promise<Profile> {
    return firstValueFrom(this.http.post<Profile>(`${this.apiUrl}/profile`, {profile}))
        .catch(error => {
            console.error('Error saving profile: ', error);
            throw error;
        });
  }
  //Updates a profile
  putProfile(): Promise<boolean> {
    return firstValueFrom(this.http.put<boolean>(`${this.apiUrl}/profile/{id}`, {}));
  }
  //Deletes a profile
  deleteProfile(profileId: string): Promise<boolean> {
    return firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}/profile/${profileId}`));
  }



}
