import { Routes } from '@angular/router';
import {ProfilesPageComponent} from './pages/profiles-page/profiles-page.component';
import {TeamsPageComponent} from './pages/teams-page/teams-page.component';
import {CurrencyPageComponent} from './pages/currency-page/currency-page.component';
import {ProfilePageComponent} from './pages/profile-page/profile-page.component';
import {ProjectsPageComponent} from './pages/projects-page/projects-page.component';
import {TeamPageComponent} from './pages/team-page/team-page.component';
import {ProjectPageComponent} from './pages/project-page/project-page.component';
import {DashboardPageComponent} from './pages/dashboard-page/dashboard-page.component';
import {LoginPageComponent} from './pages/login-page/login-page.component';
import {UsersPageComponent} from './pages/users-page/users-page.component';

export const routes: Routes = [
  { path: 'profiles', component: ProfilesPageComponent },
  { path: 'profiles/:id', component: ProfilePageComponent },
  { path: 'teams', component: TeamsPageComponent },
  { path: 'teams/:id', component: TeamPageComponent },
  { path: 'projects', component: ProjectsPageComponent },
  { path: 'projects/:id', component: ProjectPageComponent },
  { path: 'currency', component: CurrencyPageComponent },
  { path: 'dashboard', component: DashboardPageComponent },
  { path: 'login', component: LoginPageComponent },
  { path: 'users', component: UsersPageComponent },
  { path: '**', redirectTo: 'dashboard'}
];
