export interface Profile {
  profileId?: string;
  name: string;
  currency: number;
  geography: { id: number; name: string };
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
  location?: string;
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
  projectMembers: Profile[];
  projectCost?: number;
  projectMargin?: number;
  projectPrice?: number;
  startDate: Date;
  startDateString?: string;
  endDate: Date;
  endDateString?: string;
  projectMarkup: number;
  projectGrossMargin: number;
}
