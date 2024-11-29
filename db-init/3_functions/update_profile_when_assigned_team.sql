CREATE OR REPLACE FUNCTION update_profile_when_assigned_team() RETURNS TRIGGER AS $$
BEGIN
    -- update total_cost_allocation and total_hour_allocation on profile
    UPDATE profile
    SET total_cost_allocation = subquery.total_cost_allocation,
        total_hour_allocation = subquery.total_hours_allocation
    FROM (
             SELECT
                 tp.profile_id,
                 SUM(allocation_percentage_cost) AS total_cost_allocation,
                 SUM(allocation_percentage_hours) AS total_hours_allocation
             FROM team_profile tp
             WHERE tp.profile_id IN (
                 SELECT profile_id FROM team_profile WHERE team_id = NEW.team_id
             )
             GROUP BY tp.profile_id
         ) as subquery
    WHERE Profile.profile_id = subquery.profile_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER profile_team_assignment
    AFTER INSERT OR UPDATE ON team_profile
    FOR EACH ROW
EXECUTE FUNCTION update_profile_when_assigned_team();
