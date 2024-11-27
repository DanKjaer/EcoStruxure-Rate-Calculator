CREATE TABLE ProjectTeam (
                             project_team_id UUID PRIMARY KEY,
                             project_id UUID REFERENCES Project(project_id),
                             team_id UUID REFERENCES Team(team_id),
                             allocation_percentage NUMERIC(5, 2) NOT NULL
);