create function update_team_on_profile_change() returns trigger
    language plpgsql
as
$$
BEGIN
    -- Update only the teams associated with the affected profile
    UPDATE Team
    SET
        hourly_rate = subquery.hourly_rate,
        day_rate = subquery.day_rate,
        total_allocated_hours = subquery.total_allocated_hours,
        total_allocated_cost = subquery.total_allocated_cost,
        updated_at = CURRENT_TIMESTAMP
    FROM (
             SELECT
                 tp.team_id,
                 SUM(
                         CASE
                             WHEN p.annual_hours > 0 THEN p.annual_cost / p.annual_hours
                             ELSE 0
                             END
                 ) AS hourly_rate,
                 SUM(
                         CASE
                             WHEN p.annual_hours > 0 AND p.hours_per_day > 0 THEN
                                 (p.annual_cost / p.annual_hours) * p.hours_per_day
                             ELSE 0
                             END
                 ) AS day_rate,
                 SUM(p.annual_hours) AS total_allocated_hours,
                 SUM(p.annual_cost) AS total_allocated_cost
             FROM profile p
                      JOIN team_profile tp ON p.profile_id = tp.profile_id
             WHERE tp.team_id IN (
                 SELECT team_id FROM team_profile WHERE profile_id = NEW.profile_id
             )
             GROUP BY tp.team_id
         ) AS subquery
    WHERE Team.team_id = subquery.team_id;

    RETURN NEW;
END;
$$;

alter function update_team_on_profile_change() owner to postgres;

CREATE TRIGGER Profile_update_team_trigger
    AFTER UPDATE OR DELETE ON profile
    FOR EACH ROW
EXECUTE FUNCTION update_team_on_profile_change();

