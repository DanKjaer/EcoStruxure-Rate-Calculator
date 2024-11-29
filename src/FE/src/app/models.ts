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
  markupPercentage?: number;
  totalCostWithMarkup?: number;
  grossMarginPercentage?: number;
  totalCostWithGrossMargin?: number;
  hourlyRate?: number;
  dayRate?: number;
  totalAllocatedHours?: number;
  totalAllocatedCost?: number;
  updatedAt?: Date;
  updatedAtString?: string;
  archived?: boolean;
  teamProfiles?: TeamProfile[];
  geographies?: Geography[];
}

export interface TeamDTO {
  team: Team;
  teamProfiles: TeamProfile[];
}

export interface TeamProfile {
  teamProfileId?: string;
  team?: Team;
  profile?: Profile;
  allocationPercentageHours: number;
  allocatedHours?: number;
  allocationPercentageCost: number;
  allocatedCost?: number;
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
