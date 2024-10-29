import BigNumber from 'bignumber.js';

export class Profile {
  profileId!: string;
  name!: string;
  currency!: number;
  countryId!: number;
  resourceType!: boolean;
  annualCost?: number;
  annualHours?: number;
  hoursPerDay?: number;
  effectivenessPercentage?: number;
  effectiveWorkHours?: number;
  totalCostAllocation?: number;
  totalHourAllocation?: number;
  archived?: boolean;
  updatedAt?: Date;
}

export class Team {
  id!: string;
  name!: string;
  markup!: BigNumber;
  grossMargin!: BigNumber;
  is_archived?: boolean;
  dayRate?: BigNumber;
  totalAllocatedCost?: BigNumber;
  totalAllocatedHours?: BigNumber;
}

export class TeamProfiles {
  teamId?: string;
  profileId!: string;
  costAllocation!: BigNumber;
  allocatedCostOnTeam?: BigNumber;
  hourAllocation!: BigNumber;
  allocatedHoursOnTeam?: BigNumber;
  dayRateOnTeam?: BigNumber;
}
