CREATE OR REPLACE FUNCTION update_team_profile_recalc() RETURNS TRIGGER AS $$
DECLARE
    profile_annual_hours NUMERIC(15, 2);
    profile_annual_cost NUMERIC(15, 2);
BEGIN
    -- Fetch annual_hours and total_cost_allocation from the Profile table
    SELECT annual_hours, annual_cost
    INTO profile_annual_hours, profile_annual_cost
    FROM Profile
    WHERE profile_id = NEW.profile_id;

    -- Recalculate allocated_hours if allocation_percentage_hours is changed
    IF NEW.allocation_percentage_hours IS DISTINCT FROM OLD.allocation_percentage_hours AND
       profile_annual_hours != 0 THEN
        NEW.allocated_hours = (NEW.allocation_percentage_hours / 100) * profile_annual_hours;
    END IF;

    -- Recalculate allocated_cost if allocation_percentage_cost is changed
    IF NEW.allocation_percentage_cost IS DISTINCT FROM OLD.allocation_percentage_cost THEN
        NEW.allocated_cost = (NEW.allocation_percentage_cost / 100) * profile_annual_cost;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER profile_team_recalc
    BEFORE INSERT OR UPDATE ON team_profile
    FOR EACH ROW
EXECUTE FUNCTION update_team_profile_recalc();