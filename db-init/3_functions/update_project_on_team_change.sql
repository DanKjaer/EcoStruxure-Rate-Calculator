CREATE OR REPLACE FUNCTION update_project_on_team_change() RETURNS TRIGGER
    LANGUAGE plpgsql
as
$$
BEGIN
    UPDATE Project
    SET
        -- Update project_day_rate to reflect the current aggregated day rates of teams
        project_day_rate = subquery.day_rate,
        -- Calculate the new project_gross_margin
        project_gross_margin =
            CASE
                WHEN project_price > 0 THEN
                    CASE
                        -- If the project has not started, calculate gross margin using the full duration
                        WHEN CURRENT_DATE < project_start_date THEN
                            ((project_price - (subquery.day_rate * project_total_days))
                                / project_price) * 100
                        -- If the project has started, account for historical cost and projected cost
                        ELSE
                            ((project_price - COALESCE(project_total_cost_at_change, 0)
                                - (subquery.day_rate * (project_end_date - COALESCE(project_rest_cost_date, project_start_date))))
                                / project_price) * 100
                        END
                ELSE 0
                END,
        -- Update the project_total_cost_at_change only if the project has started
        project_total_cost_at_change =
            CASE
                WHEN CURRENT_DATE >= project_start_date THEN
                    COALESCE(project_total_cost_at_change, 0) +
                    (project_day_rate * (CURRENT_DATE - COALESCE(project_rest_cost_date, project_start_date)))
                ELSE project_total_cost_at_change -- Keep unchanged if the project hasn't started
                END,
        -- Update the project_rest_cost_date only if the project has started
        project_rest_cost_date =
            CASE
                WHEN CURRENT_DATE >= project_start_date THEN CURRENT_DATE
                ELSE project_rest_cost_date -- Keep unchanged if the project hasn't started
                END
    FROM (
             SELECT
                 pt.project_id,
                 SUM(
                         (t.day_rate * (1 + t.markup_percentage / 100)) * (pt.allocation_percentage / 100)
                 ) AS day_rate
             FROM team t
                      JOIN project_team pt ON t.team_id = pt.team_id
             WHERE pt.team_id = NEW.team_id
             GROUP BY pt.project_id
         ) AS subquery
    WHERE Project.project_id = subquery.project_id;

    RETURN NEW;
END;
$$;

CREATE TRIGGER team_update_project_trigger
    AFTER UPDATE OR DELETE ON team
    FOR EACH ROW
EXECUTE FUNCTION update_project_on_team_change();
