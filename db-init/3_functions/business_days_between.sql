CREATE OR REPLACE FUNCTION business_days_between(start_date DATE, end_date DATE)
    RETURNS INTEGER AS
$$
DECLARE
    day_count INTEGER := 0;
    current_day DATE := start_date;
BEGIN
    -- Loop through each date between the start and end date
    WHILE current_day <= end_date LOOP
            -- Check if the current date is a weekday (Monday to Friday)
            IF EXTRACT(DOW FROM current_day) NOT IN (0, 6) THEN  -- 0 is Sunday, 6 is Saturday
                day_count := day_count + 1;
            END IF;
            -- Increment the current date by 1 day
                    current_day := (current_day + INTERVAL '1 day')::DATE;
        END LOOP;

    RETURN day_count;
END;
$$ LANGUAGE plpgsql;