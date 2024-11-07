import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom} from 'rxjs';
import {Project} from '../models';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8080/api/projects';
  //Gets a list of projects
  async getProjects():Promise<Project[]> {
    return firstValueFrom(this.http.get<Project[]>(`${this.apiUrl}/all`));
  }
  //Gets a project
  async getProject(): Promise<Project> {
    return firstValueFrom(this.http.get<Project>(`${this.apiUrl}`));
  }
  //Creates a project
  async postProject(project: Project): Promise<Project> {
    return firstValueFrom(this.http.post<Project>(`${this.apiUrl}`, {project}));
  }
}
