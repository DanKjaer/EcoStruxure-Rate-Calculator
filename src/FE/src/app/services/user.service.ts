import {HttpClient} from '@angular/common/http';
import {firstValueFrom} from 'rxjs';
import {User} from '../models';

export class UserService{

  constructor(private http: HttpClient) {
  }

  private apiUrl = 'http://localhost:8080/api/users';

  async getUsers(): Promise<User[]> {
    return firstValueFrom(this.http.get<User[]>(`${this.apiUrl}/all`));
  }

  async postUser(user: User): Promise<User> {
    return firstValueFrom(this.http.post<User>(`${this.apiUrl}`, {user}));
  }

  deleteUser(userId: string): Promise<boolean> {
    return firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}/${userId}`));
  }
}
