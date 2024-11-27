CREATE TABLE Profile (
                         profile_id UUID PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         geography_id INT REFERENCES Geography(id),
                         resource_type BOOLEAN,
                         annual_cost NUMERIC(15, 2) NOT NULL,
                         annual_hours NUMERIC(15, 2) NOT NULL,
                         hours_per_day NUMERIC(15, 2),
                         effectiveness_percentage NUMERIC(5, 2),
                         effective_work_hours NUMERIC(15, 2),
                         total_cost_allocation NUMERIC(15, 2),
                         total_hour_allocation NUMERIC(15, 2),
                         archived BOOLEAN DEFAULT FALSE,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);