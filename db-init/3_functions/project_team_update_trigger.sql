-- Using function from update_project_on_team_change,
-- since its same stuff needed to be done.

CREATE TRIGGER project_team_update_trigger
    AFTER INSERT OR UPDATE OR DELETE ON project_team
    FOR EACH ROW
EXECUTE FUNCTION update_project_on_team_change();