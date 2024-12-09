export interface Profile {
  profileId?: string;
  name: string;
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
  name?: string;
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

export interface TeamProfile {
  teamProfileId?: string;
  team?: Team;
  profile?: Profile;
  allocationPercentageHours?: number;
  allocatedHours?: number;
  allocationPercentageCost?: number;
  allocatedCost?: number;
}

export interface TeamDTO {
  teamId?: string;
  name: string;
  markupPercentage: number;
  totalCostWithMarkup: number;
  grossMarginPercentage: number;
  totalCostWithGrossMargin: number;
  hourlyRate: number;
  dayRate: number;
  totalAllocatedHours: number;
  totalAllocatedCost: number;
  updatedAt: Date;
  archived: boolean;
  teamProfiles: TeamProfileDTO[];
  geographies: Geography[];
}

export interface TeamProfileDTO {
  teamProfileId?: string;
  team: Team | string;
  profile: Profile;
  allocationPercentageHours: number;
  allocatedHours: number;
  allocationPercentageCost: number;
  allocatedCost: number;
}

export interface Geography {
  id: number;
  name: string;
}

export interface Project {
  projectId?: string;
  projectName: string;
  projectDescription: string;
  projectTeams: ProjectTeam[];
  projectDayRate?: number;
  projectGrossMargin?: number;
  projectPrice?: number;
  projectTotalCostAtChange?: number;
  projectTotalDays?: number;
  projectStartDate: Date;
  projectEndDate: Date;
  projectLocation: Geography;
  projectArchived?: boolean;
  projectRestCostDate?: Date;
  projectSalesNumber?: number;
  startDateString?: string;
  endDateString?: string;
  projectMembersString?: string;
}

export interface ProjectTeam {
  projectTeamId?: string;
  team: Team;
  project?: Project | string;
  allocationPercentage?: number;
}

export interface Currency {
  currencyCode: string;
  eurConversionRate: number;
  symbol: string;
}
