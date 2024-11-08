import { Routes } from '@angular/router';
import {ProfilesPageComponent} from './pages/profiles-page/profiles-page.component';
import {TeamsPageComponent} from './pages/teams-page/teams-page.component';
import {CurrencyPageComponent} from './pages/currency-page/currency-page.component';
import {ProfilePageComponent} from './pages/profile-page/profile-page.component';
import {ProjectPageComponent} from './pages/project-page/project-page.component';
import {TeamPageComponent} from './pages/team-page/team-page.component';

export const routes: Routes = [
  { path: 'profiles', component: ProfilesPageComponent },
  { path: 'teams', component: TeamsPageComponent },
  { path: 'currency', component: CurrencyPageComponent },
  { path: 'profile/:id', component: ProfilePageComponent },
  { path: 'projects', component: ProjectPageComponent },
  { path: 'team/:id', component: TeamPageComponent },
];
