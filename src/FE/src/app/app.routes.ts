import { Routes } from '@angular/router';
import {ProfilesPageComponent} from './profiles-page/profiles-page.component';
import {TeamsPageComponent} from './teams-page/teams-page.component';
import {CurrencyPageComponent} from './currency-page/currency-page.component';
import {ProfilePageComponent} from './profile-page/profile-page.component';
import {ProjectPageComponent} from './project-page/project-page.component';

export const routes: Routes = [
  { path: 'profiles', component: ProfilesPageComponent },
  { path: 'teams', component: TeamsPageComponent },
  { path: 'currency', component: CurrencyPageComponent },
  { path: 'profile/:id', component: ProfilePageComponent },
  { path: 'projects', component: ProjectPageComponent }
];
