CREATE TABLE TeamProfile (
                             team_profile_id UUID PRIMARY KEY,
                             team_id UUID REFERENCES Team(team_id),
                             profile_id UUID REFERENCES Profile(profile_id),
                             allocation_percentage NUMERIC(5, 2) NOT NULL,
                             allocated_cost NUMERIC(15, 2)
);