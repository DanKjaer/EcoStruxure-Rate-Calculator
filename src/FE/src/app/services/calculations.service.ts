import { Injectable } from '@angular/core';
import {ProjectTeam, Team, TeamProfile} from '../models';

@Injectable({
  providedIn: 'root'
})
export class CalculationsService {

  constructor() { }

  public calculateCostAllocation(teamProfile: TeamProfile): number {
    if (teamProfile.allocationPercentageCost) {
      return teamProfile.profile!.annualCost! * (teamProfile.allocationPercentageCost / 100);
    }
    return 0;
  }

  public calculateHourAllocation(teamProfile: TeamProfile): number {
    if (teamProfile.allocationPercentageHours) {
      return teamProfile.profile!.annualHours! * (teamProfile.allocationPercentageHours / 100);
    }
    return 0;
  }

  public calculateDayRateForTeam(projectTeam: ProjectTeam): number {
    let dayRate = projectTeam.team.dayRate!;
    let markup = projectTeam.team.markupPercentage!;
    let dayRateWithMarkup = dayRate * (1 + (markup / 100));
    return dayRateWithMarkup * (projectTeam.allocationPercentage! / 100);
  }

  public calculateDayRateForProfile(teamProfile: TeamProfile) {
    if (teamProfile.allocatedHours === 0) {
      return 0;
    }
    let hourlyRate = teamProfile.allocatedCost! / teamProfile.allocatedHours!;
    return hourlyRate * 8;
  }
}
