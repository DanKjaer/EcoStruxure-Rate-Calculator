CREATE OR REPLACE FUNCTION update_team_recalc()
    RETURNS TRIGGER AS $$
BEGIN
    -- Recalculate team's total markup and total gross margin, if change in markup or gross_margin
    IF NEW.markup_percentage IS DISTINCT FROM OLD.markup_percentage
        OR NEW.gross_margin_percentage IS DISTINCT FROM OLD.gross_margin_percentage THEN
        IF NEW.markup_percentage IS NOT NULL AND NEW.gross_margin_percentage IS NOT NULL THEN
            NEW.total_cost_with_markup := (1 + NEW.markup_percentage / 100) * NEW.total_allocated_cost;
            NEW.total_cost_with_gross_margin := (1 + NEW.gross_margin_percentage / 100) * NEW.total_cost_with_markup;
        END IF;
    END IF;

    -- Update the updated_at column
    NEW.updated_at := CURRENT_TIMESTAMP;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Attach the trigger to the team table
CREATE TRIGGER team_update_trigger_recalc
    BEFORE UPDATE ON team
    FOR EACH ROW
EXECUTE FUNCTION update_team_recalc();
