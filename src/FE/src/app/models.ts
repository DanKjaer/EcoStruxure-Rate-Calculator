export interface Profile {
  profileId?: string;
  name: string;
  currency: string;
  geography: Geography;
  resourceType: boolean;
  annualCost?: number;
  annualHours?: number;
  hoursPerDay?: number;
  effectivenessPercentage?: number;
  effectiveWorkHours?: number;
  totalCostAllocation?: number;
  totalHourAllocation?: number;
  archived?: boolean;
  updatedAt?: Date;
  isEditing?: boolean;
}

export interface Team {
  teamId?: string; // UUID
  name: string;
  markup?: number;
  grossMargin?: number;
  isArchived?: boolean;
  updatedAt?: Date;
  updatedAtString?: string;
  hourlyRate?: number;
  dayRate?: number;
  totalAllocatedCost?: number;
  totalAllocatedHours?: number;
  totalMarkup?: number;
  totalGrossMargin?: number;
  geographies?: Geography[];
}

export interface TeamDTO {
  team: Team;
  teamProfiles: TeamProfiles[];
}

export interface TeamProfiles {
  teamId?: string;
  profileId: string;
  name: string;
  annualCost: number;
  annualHours: number;
  costAllocation: number;
  allocatedCostOnTeam?: number;
  hourAllocation: number;
  allocatedHoursOnTeam?: number;
  dayRateOnTeam?: number;
  geography: Geography;
}

export interface Geography {
  id: number;
  name: string;
  predefined?: boolean;
}

export interface Project {
  projectId?: string;
  projectName: string;
  projectDescription: string;
  projectMembers: ProjectMembers[];
  projectPrice?: number;
  projectStartDate: Date;
  startDateString?: string;
  projectEndDate: Date;
  endDateString?: string;
  projectTotalDays?: number;
  projectLocation: Geography;
  projectArchived?: boolean;
  projectDayRate?: number;
  projectSalesNumber?: number;
  projectGrossMargin?: number;
  projectMembersString?: string;
}

export interface ProjectMembers {
  teamId: string;
  projectId: string;
  name: string;
  projectAllocation: number;
  markup?: number;
  dayRate?: number;
  dayRateWithMarkup?: number;
}

export interface Currency {
  currencyCode: string;
  eurConversionRate: number;
  usdConversionRate: number;
  symbol: string;
}
