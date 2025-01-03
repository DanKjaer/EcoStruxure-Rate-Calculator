import {HttpClient, HttpHeaders} from '@angular/common/http';
import {firstValueFrom} from 'rxjs';
import {User} from '../models';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserService{

  constructor(private http: HttpClient) {
  }

  private apiUrl = 'http://localhost:8080/api/user';

  async getUsers(): Promise<User[]> {
    return firstValueFrom(this.http.get<User[]>(`${this.apiUrl}`));
  }

  async postUser(user: User): Promise<User> {
    return firstValueFrom(this.http.post<User>(`${this.apiUrl}/register`, user));
  }

  deleteUser(userId: string): Promise<boolean> {
    return firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}?id=${userId}`));
  }

  putUser(user: User): Promise<User> {
    return firstValueFrom(this.http.put<User>(`${this.apiUrl}`, user));
  }
}
