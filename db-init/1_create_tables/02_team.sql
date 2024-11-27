CREATE TABLE Team (
                      team_id UUID PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      markup NUMERIC(15, 2),
                      total_markup NUMERIC(15, 2),
                      gross_margin NUMERIC(15, 2),
                      total_gross_margin NUMERIC(15, 2),
                      hourly_rate NUMERIC(15, 2),
                      day_rate NUMERIC(15, 2),
                      total_allocated_hours NUMERIC(15, 2),
                      total_allocated_cost NUMERIC(15, 2),
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      archived BOOLEAN DEFAULT FALSE
);