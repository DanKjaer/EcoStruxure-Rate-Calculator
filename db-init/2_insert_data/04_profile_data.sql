INSERT INTO dbo.Profiles (
    profile_id,
    name,
    currency,
    geography_id,
    resource_type,
    is_archived,
    annual_cost,
    effectiveness,
    annual_hours,
    effective_work_hours,
    hours_per_day,
    total_cost_allocation,
    total_hour_allocation
)
VALUES
    ('24c2b2f8-47f0-4c8b-b55d-cb49996feb44', 'Cheap profile 1', 'EUR', 39, FALSE, FALSE, 40000.0000, 80, 2080.00, 1664, 8.00, 100.00, 100.00), -- IT consultant in North America
    ('44656c14-5882-4247-a0eb-7dc9253ec923', 'Cheap profile 2', 'EUR', 13, FALSE, FALSE, 40000.0000, 80, 2080.00, 1664, 8.00,100.00, 100.00), -- Project manager in Europe
    ('071495d6-cfce-46f2-a6fd-5a77ca4fbdea', 'Cheap profile 3', 'EUR', 107, FALSE, FALSE, 40000.0000, 80, 2080.00, 1664, 8.00,100.00, 100.00), -- Developer in Asia
    ('869877f5-b85c-4abb-890a-8de561c82641', 'Cheap manager', 'EUR', 261, TRUE, FALSE, 40000.0000, 80, 0.00, 0, 8.00,100.00, 100.00), -- Support specialist in Latin America
    ('a2f4d5d8-bd00-4f6d-a3d9-51cf4463d91e', 'Medium profile 1', 'EUR', 2, FALSE, FALSE, 80000.0000, 80, 2080.00, 1664, 8.00,100.00, 100.00), -- Security analyst in the Middle East
    ('5910ffa5-6ef2-44d6-9d89-a7d642810536', 'Medium profile 2', 'EUR', 39, FALSE, FALSE, 80000.0000, 80, 2080.00, 1664, 8.00,100.00, 100.00), -- Part-time executive in Canada
    ('0a2d65d5-e379-4145-a153-a4f509954b65', 'Medium profile 3', 'EUR', 13, FALSE, FALSE, 80000.0000, 80, 2080.00, 1664, 8.00,100.00, 100.00), -- Full-time consultant in Germany
    ('317f43ef-569c-4a1b-b881-edad7eb79db7', 'High profile 1', 'EUR', 107, FALSE, FALSE, 120000.0000, 80, 2080.00, 1664, 8.00,100.00, 100.00), -- Part-time developer in India
    ('92ac4d45-af92-475c-acb1-af2c626d86b0', 'High profile 2', 'EUR', 78, FALSE, FALSE, 120000.0000, 80, 2080.00, 1664, 8.00,100.00, 100.00), -- Part-time project manager in the UK
    ('2b485563-7c7f-49e7-b58f-b1b9fd2bbadf', 'High profile 3', 'EUR', 14, FALSE, FALSE, 120000.0000, 80, 2080.00, 1664, 8.00,100.00, 100.00), -- Overtime specialist in Australia
    ('50b601e3-dd42-422b-ac43-1aae17dff125', 'High medium manager', 'EUR', 32, True, FALSE, 120000.0000, 80, 0.00, 0, 8.00,100.00, 100.00); -- Standard worker in Brazil
