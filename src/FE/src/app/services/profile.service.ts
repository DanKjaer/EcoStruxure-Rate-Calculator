import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom} from 'rxjs';
import {Profile} from '../models';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  private apiUrl = 'http://localhost:8080/api/profile';
  constructor(private http: HttpClient) { }

  /**
   * Gets a list of profiles.
   */
  getProfiles(): Promise<Profile[]> {
    return firstValueFrom(this.http.get<Profile[]>(`${this.apiUrl}/all`));
  }

  /**
   * Gets a profile by id.
   * @param id
   */
  getProfile(id: string): Promise<Profile> {
    return firstValueFrom(this.http.get<Profile>(`${this.apiUrl}?id=${id}`));
  }

  /**
   * Creates a profile.
   * @param profile
   */
  postProfile(profile: Profile): Promise<Profile> {
    return firstValueFrom(this.http.post<Profile>(`${this.apiUrl}`, profile));
  }

  /**
   * updates a profile.
   */
  putProfile(profile: Profile): Promise<Profile> {
    return firstValueFrom(this.http.put<Profile>(`${this.apiUrl}`, profile));
  }

  /**
   * Deletes a profile by id.
   * @param profileId
   */
  deleteProfile(profileId: string): Promise<boolean> {
    return firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}?id=${profileId}`));
  }

}
