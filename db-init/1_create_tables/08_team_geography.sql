CREATE TABLE team_geography (
    team_id UUID REFERENCES Team(team_id),
    geopgraphy_id int REFERENCES Geography(id) NOT NULL ,
    PRIMARY KEY (team_id, geopgraphy_id)
)