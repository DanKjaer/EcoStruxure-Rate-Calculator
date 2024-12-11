import { Injectable } from '@angular/core';
import {Team, TeamDTO, TeamProfileDTO} from '../models';

@Injectable({
  providedIn: 'root'
})
export class GenerateDTOService {

  constructor() { }

  public createTeamDTO(team: Team): TeamDTO {
    let teamProfiles = team.teamProfiles!.map(teamProfile => {
      let teamProfileDTO: TeamProfileDTO = {
        teamProfileId: teamProfile.teamProfileId!,
        team: team.teamId!,
        profile: teamProfile.profile!,
        allocationPercentageHours: teamProfile.allocationPercentageHours!,
        allocatedHours: teamProfile.allocatedHours!,
        allocationPercentageCost: teamProfile.allocationPercentageCost!,
        allocatedCost: teamProfile.allocatedCost!,
      };
      return teamProfileDTO;
    });
    let teamDTO: TeamDTO = {
      teamId: team.teamId,
      name: team.name!,
      markupPercentage: team.markupPercentage!,
      totalCostWithMarkup: team.totalCostWithMarkup!,
      grossMarginPercentage: team.grossMarginPercentage!,
      totalCostWithGrossMargin: team.totalCostWithGrossMargin!,
      hourlyRate: team.hourlyRate!,
      dayRate: team.dayRate!,
      totalAllocatedHours: team.totalAllocatedHours!,
      totalAllocatedCost: team.totalAllocatedCost!,
      updatedAt: team.updatedAt!,
      archived: team.archived!,
      teamProfiles: teamProfiles!,
      geographies: team.geographies!,
    };
    return teamDTO;
  }
}
