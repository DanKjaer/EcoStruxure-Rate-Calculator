CREATE OR REPLACE FUNCTION update_profile_hours_recalc()
    RETURNS TRIGGER AS $$
BEGIN
    -- Recalculate effective_work_hours if annual_hours or effectiveness_percentage changes
    IF NEW.annual_hours IS DISTINCT FROM OLD.annual_hours
        OR NEW.effectiveness_percentage IS DISTINCT FROM OLD.effectiveness_percentage THEN
        IF NEW.annual_hours IS NOT NULL AND NEW.effectiveness_percentage IS NOT NULL THEN
            NEW.effective_work_hours :=
                    NEW.annual_hours * (NEW.effectiveness_percentage / 100);
        ELSE
            NEW.effective_work_hours := NULL; -- Reset if dependent fields are NULL
        END IF;
    END IF;

    -- Update the updated_at column
    NEW.updated_at := CURRENT_TIMESTAMP;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Attach the trigger to the Profile table
CREATE TRIGGER profile_update_trigger_hours_recalc
    BEFORE UPDATE ON Profile
    FOR EACH ROW
EXECUTE FUNCTION update_profile_hours_recalc();
