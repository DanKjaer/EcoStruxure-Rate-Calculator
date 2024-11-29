CREATE OR REPLACE FUNCTION update_project_recalc()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.project_end_date IS DISTINCT FROM OLD.project_end_date OR
       NEW.project_start_date IS DISTINCT FROM OLD.project_start_date THEN
        NEW.project_total_days := NEW.project_end_date - NEW.project_start_date;
        NEW.project_total_days := business_days_between(NEW.project_start_date, NEW.project_end_date);
    END IF;

    IF NEW.project_price IS DISTINCT FROM OLD.project_price THEN
        IF NEW.project_price IS NOT NULL THEN

            IF CURRENT_DATE < NEW.project_start_date THEN
                NEW.project_gross_margin :=
                        ((NEW.project_price - (NEW.project_day_rate * NEW.project_total_days)) / NEW.project_price) *
                        100;
            ELSE
                NEW.project_gross_margin := ((NEW.project_price - COALESCE(NEW.project_total_cost_at_change, 0) -
                                              (NEW.project_day_rate * (NEW.project_end_date -
                                                                       COALESCE(NEW.project_rest_cost_date,
                                                                                NEW.project_start_date)))) /
                                             NEW.project_price) * 100;
            END IF;
        ELSE
            NEW.project_gross_margin := 0;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER project_update_trigger_recalc
    BEFORE UPDATE
    ON project
    FOR EACH ROW
EXECUTE FUNCTION update_project_recalc();
