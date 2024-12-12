import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../models';
import {firstValueFrom} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private apiUrl = 'http://localhost:8080/api/user';
  constructor(private http: HttpClient) { }

  /**
   * Authenticates a user.
   * @param username
   * @param password
   */
  public async authenticate(user: User): Promise<boolean> {
    try {
      const response = await firstValueFrom(this.http.post<{ token: string }>(`${this.apiUrl}/authenticate`, user));
      if (response.token) {
        localStorage.setItem('EcoStructureProjectJWT', response.token);
        return true;
      }
      return false;
    } catch (error) {
      console.error('Authentication failed:', error);
      return false;
    }
  }
}
