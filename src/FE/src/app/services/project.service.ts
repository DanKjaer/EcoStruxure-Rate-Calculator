import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom} from 'rxjs';
import {Project} from '../models';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  constructor(private http: HttpClient) {
  }

  private apiUrl = 'http://localhost:8080/api/projects';

  /**
   * Gets a list of projects
   */
  async getProjects(): Promise<Project[]> {
    return firstValueFrom(this.http.get<Project[]>(`${this.apiUrl}/all`));
  }

  /**
   * Gets a project
   */
  async getProject(id: string): Promise<Project> {
    return firstValueFrom(this.http.get<Project>(`${this.apiUrl}?projectId=${id}`));
  }

  /**
   * Creates a project
   * @param project
   */
  async postProject(project: Project): Promise<Project> {
    return firstValueFrom(this.http.post<Project>(`${this.apiUrl}`, {project}));
  }

  /**
   * Deletes a project
   */
   deleteProject(projectId: string): Promise<boolean> {
    return firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}/${projectId}`));
  }

  /**
   * Updates a project
   * @param selectedProject
   */
  async putProject(selectedProject: Project): Promise<Project> {
    return firstValueFrom(this.http.put<Project>(`${this.apiUrl}/update`, selectedProject));
  }
}
