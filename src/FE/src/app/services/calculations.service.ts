import { Injectable } from '@angular/core';
import {TeamProfile} from '../models';

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
}
