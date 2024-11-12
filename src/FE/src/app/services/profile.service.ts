import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom, Observable} from 'rxjs';
import {Profile} from '../models';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  private apiUrl = 'http://localhost:8080/api/profile';
  constructor(private http: HttpClient) { }

  /**
   * Gets a list of profiles
   */
  getProfiles(): Promise<Profile[]> {
    return firstValueFrom(this.http.get<Profile[]>(`${this.apiUrl}/all`));
  }

  getProfile(id: string): Promise<Profile> {
    return firstValueFrom(this.http.get<Profile>(`${this.apiUrl}?id=${id}`));
  }

  /**
   * Gets a profile
   * @param profile
   */
  postProfile(profile: Profile): Promise<Profile> {
    return firstValueFrom(this.http.post<Profile>(`${this.apiUrl}`, {profile}))
        .catch(error => {
            console.error('Error saving profile: ', error);
            throw error;
        });
  }

  /**
   * updates a profile
   */
  putProfile(): Promise<boolean> {
    return firstValueFrom(this.http.put<boolean>(`${this.apiUrl}/{id}`, {}));
  }

  /**
   * Deletes a profile
   * @param profileId
   */
  deleteProfile(profileId: string): Promise<boolean> {
    return firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}/${profileId}`));
  }
}
