CREATE TABLE team_profile (
                             team_profile_id UUID PRIMARY KEY,
                             team_id UUID REFERENCES Team(team_id),
                             profile_id UUID REFERENCES Profile(profile_id),
                             allocation_percentage_hours NUMERIC(5, 2) NOT NULL, -- Allocation of time (e.g., 75%)
                             allocated_hours NUMERIC(15, 2), -- Pre-calculated: annual_hours * allocation_percentage_hours / 100
                             allocation_percentage_cost NUMERIC(5, 2) NOT NULL, -- Allocation of cost (e.g., 50%)
                             allocated_cost NUMERIC(15, 2) -- Pre-calculated: annual_cost * allocation_percentage_cost / 100
);