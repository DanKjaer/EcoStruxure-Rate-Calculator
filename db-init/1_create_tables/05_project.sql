CREATE TABLE Project (
                         project_id UUID PRIMARY KEY,
                         project_name VARCHAR(255) NOT NULL,
                         project_sales_number VARCHAR(50) UNIQUE,
                         project_description TEXT,
                         project_day_rate NUMERIC(15, 2),
                         project_gross_margin NUMERIC(15, 2),
                         project_price NUMERIC(15, 2),
                         project_start_date DATE,
                         project_end_date DATE,
                         project_total_days INT,
                         project_location_id INT REFERENCES Geography(id),
                         project_archived BOOLEAN DEFAULT FALSE,
                         project_total_cost_at_change NUMERIC(15, 2),
                         project_rest_cost_date DATE
);
