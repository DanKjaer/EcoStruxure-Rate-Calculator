export interface Profile {
  profileId?: string;
  name: string;
  currency: number;
  countryId: number;
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
  teamId?: string;
  name: string;
  markup?: number;
  grossMargin?: number;
  isArchived?: boolean;
  updatedAt?: Date;
  hourlyRate?: number;
  dayRate?: number;
  totalAllocatedCost?: number;
  totalAllocatedHours?: number;
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
  projectCost: number;
  projectMargin: number;
  projectPrice: number;
  endDate: Date;
}
