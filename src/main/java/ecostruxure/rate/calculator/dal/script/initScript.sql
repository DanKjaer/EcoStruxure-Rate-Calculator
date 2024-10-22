DROP TABLE IF EXISTS dbo.Teams_profiles_history;

DROP TABLE IF EXISTS dbo.Teams_profiles;

DROP TABLE IF EXISTS dbo.Profiles_history;

DROP TABLE IF EXISTS dbo.Profiles;

DROP TABLE IF EXISTS dbo.Teams;

DROP TABLE IF EXISTS dbo.Currency;

DROP TABLE IF EXISTS dbo.geography_countries;

DROP TABLE IF EXISTS dbo.geography;

DROP TABLE IF EXISTS dbo.Countries;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE dbo.Currency (
                              currency_code       VARCHAR(3) PRIMARY KEY NOT NULL,
                              eur_conversion_rate DECIMAL(10, 2),
                              usd_conversion_rate DECIMAL(10, 2)
);

CREATE TABLE dbo.Teams (
                           id                          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           name                        VARCHAR(100)  NOT NULL,
                           markup                      DECIMAL(5, 2) DEFAULT 0 NOT NULL,
                           gross_margin                DECIMAL(5, 2) DEFAULT 0 NOT NULL,
                           is_archived                 BOOLEAN DEFAULT FALSE,
                           updated_at                  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           day_rate                    DECIMAL(10, 2),
                           hourly_rate                 DECIMAL(10, 2),
                           total_allocated_cost        DECIMAL(19, 2),
                           total_allocated_hours       DECIMAL(10, 2)
);

CREATE TABLE dbo.Countries (
                               code VARCHAR(10) PRIMARY KEY NOT NULL,
                               latitude DECIMAL(9, 6) NOT NULL,
                               longitude DECIMAL(9, 6) NOT NULL,
                               CONSTRAINT UC_Countries_Code UNIQUE (code) -- sikrer at hver data i code rækken er unik
);

CREATE TABLE dbo.geography (
                               id          SERIAL PRIMARY KEY,
                               name        VARCHAR(100),
                               predefined  INTEGER DEFAULT 0,
                               country     INTEGER DEFAULT 0
);

CREATE TABLE dbo.geography_countries (
                                         geography    int NOT NULL,
                                         code         VARCHAR(10) NOT NULL,
                                         FOREIGN KEY (geography) REFERENCES geography(id),
                                         FOREIGN KEY (code) REFERENCES Countries(code)
);


CREATE TABLE dbo.Profiles (
                              profile_id              UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
                              name                    TEXT NOT NULL,
                              currency                TEXT NOT NULL,
                              country_id              INT NOT NULL,
                              resource_type           BOOLEAN NOT NULL,
                              annual_cost             DECIMAL(19, 4), -- DECIMAL(precision, scale), up to 999999999999999.9999
                              effectiveness           DECIMAL(6, 2), --  -999.99 - 999.99
                              annual_hours            DECIMAL(7, 2),
                              effective_work_hours    DECIMAL(10, 2),
                              hours_per_day           DECIMAL(4, 2), -- -99.99 - 99.99
                              is_archived             BOOLEAN DEFAULT FALSE,
                              updated_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                              FOREIGN KEY (country_id) REFERENCES geography(id)
);


CREATE TABLE dbo.Profiles_history (
                                      history_id               UUID PRIMARY KEY NOT NULL,
                                      profile_id               UUID NOT NULL,
                                      resource_type            BOOLEAN DEFAULT FALSE,
                                      annual_cost              DECIMAL(19, 4),
                                      effectiveness            DECIMAL(6, 2),
                                      annual_hours             DECIMAL(7, 2) NOT NULL,
                                      hours_per_day            DECIMAL(4, 2) NOT NULL,
                                      updated_at               TIMESTAMP NOT NULL,
                                      FOREIGN KEY (profile_id) REFERENCES Profiles(profile_id)
);


CREATE TABLE dbo.Teams_profiles (
                                    teamId                       UUID NOT NULL,
                                    profileId                    UUID,
                                    cost_allocation              DECIMAL(10, 2) NOT NULL,
                                    allocated_cost_on_team       DECIMAL(10, 2),
                                    hour_allocation              DECIMAL(10, 2) NOT NULL,
                                    allocated_hours_on_team       DECIMAL(10, 2),
                                    day_rate_on_team             DECIMAL(10, 2),
                                    FOREIGN KEY (teamId) REFERENCES Teams(id),
                                    FOREIGN KEY (profileId) REFERENCES Profiles(profile_id)
);


CREATE TABLE dbo.Teams_profiles_history (
                                            id                               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                            team_id                          UUID NOT NULL,
                                            profile_id                       UUID,
                                            profile_history_id               UUID,
                                            reason                           VARCHAR(50),
                                            hourly_rate                      DECIMAL(10, 2),
                                            day_rate                         DECIMAL(12, 2),
                                            annual_cost                      DECIMAL(19, 2),
                                            annual_hours                     DECIMAL(10, 2),
                                            cost_allocation                  DECIMAL(5, 2),
                                            hour_allocation                  DECIMAL(5, 2),
                                            profile_hourly_rate              DECIMAL(10, 2),
                                            profile_day_rate                 DECIMAL(12, 2),
                                            profile_annual_cost              DECIMAL(19, 2),
                                            profile_annual_hours             DECIMAL(10, 2),
                                            updated_at                       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                            FOREIGN KEY (team_id)            REFERENCES Teams(id),
                                            FOREIGN KEY (profile_id)         REFERENCES Profiles(profile_id),
                                            FOREIGN KEY (profile_history_id) REFERENCES Profiles_history(history_id)
);


INSERT INTO dbo.Countries (code, latitude, longitude) VALUES
                                                          ('AD', 42.546245, 1.601554),
                                                          ('AE', 23.424076, 53.847818),
                                                          ('AF', 33.93911, 67.709953),
                                                          ('AG', 17.060816, -61.796428),
                                                          ('AI', 18.220554, -63.068615),
                                                          ('AL', 41.153332, 20.168331),
                                                          ('AM', 40.069099, 45.038189),
                                                          ('AN', 12.226079, -69.060087),
                                                          ('AO', -11.202692, 17.873887),
                                                          ('AQ', -75.250973, -0.071389),
                                                          ('AR', -38.416097, -63.616672),
                                                          ('AS', -14.270972, -170.132217),
                                                          ('AT', 47.516231, 14.550072),
                                                          ('AU', -25.274398, 133.775136),
                                                          ('AW', 12.52111, -69.968338),
                                                          ('AX', 60.241034, 20.063198),
                                                          ('AZ', 40.143105, 47.576927),
                                                          ('BA', 43.915886, 17.679076),
                                                          ('BB', 13.193887, -59.543198),
                                                          ('BD', 23.684994, 90.356331),
                                                          ('BE', 50.503887, 4.469936),
                                                          ('BF', 12.238333, -1.561593),
                                                          ('BG', 42.733883, 25.48583),
                                                          ('BH', 25.930414, 50.637772),
                                                          ('BI', -3.373056, 29.918886),
                                                          ('BJ', 9.30769, 2.315834),
                                                          ('BL', 17.901325, -62.823085),
                                                          ('BM', 32.321384, -64.75737),
                                                          ('BN', 4.535277, 114.727669),
                                                          ('BO', -16.290154, -63.588653),
                                                          ('BQ', 12.137575, -68.264719),
                                                          ('BR', -14.235004, -51.92528),
                                                          ('BS', 25.03428, -77.39628),
                                                          ('BT', 27.514162, 90.433601),
                                                          ('BV', -54.423199, 3.413194),
                                                          ('BW', -22.328474, 24.684866),
                                                          ('BY', 53.709807, 27.953389),
                                                          ('BZ', 17.189877, -88.49765),
                                                          ('CA', 56.130366, -106.346771),
                                                          ('CC', -12.164165, 96.870956),
                                                          ('CD', -4.038333, 21.758664),
                                                          ('CF', 6.611111, 20.939444),
                                                          ('CG', -0.228021, 15.827659),
                                                          ('CH', 46.818188, 8.227512),
                                                          ('CI', 7.539989, -5.54708),
                                                          ('CK', -21.236736, -159.777671),
                                                          ('CL', -35.675147, -71.542969),
                                                          ('CM', 7.369722, 12.354722),
                                                          ('CN', 35.86166, 104.195397),
                                                          ('CO', 4.570868, -74.297333),
                                                          ('CR', 9.748917, -83.753428),
                                                          ('CU', 21.521757, -77.781167),
                                                          ('CV', 16.002082, -24.013197),
                                                          ('CW', 12.16957, -68.990021),
                                                          ('CX', -10.447525, 105.690449),
                                                          ('CY', 35.126413, 33.429859),
                                                          ('CZ', 49.817492, 15.472962),
                                                          ('DE', 51.165691, 10.451526),
                                                          ('DJ', 11.825138, 42.590275),
                                                          ('DK', 56.26392, 9.501785),
                                                          ('DM', 15.414999, -61.370976),
                                                          ('DO', 18.735693, -70.162651),
                                                          ('DZ', 28.033886, 1.659626),
                                                          ('EC', -1.831239, -78.183406),
                                                          ('EE', 58.595272, 25.013607),
                                                          ('EG', 26.820553, 30.802498),
                                                          ('EH', 24.215527, -12.885834),
                                                          ('ER', 15.179384, 39.782334),
                                                          ('ES', 40.463667, -3.74922),
                                                          ('ET', 9.145, 40.489673),
                                                          ('FI', 61.92411, 25.748151),
                                                          ('FJ', -16.578193, 179.414413),
                                                          ('FK', -51.796253, -59.523613),
                                                          ('FM', 7.425554, 150.550812),
                                                          ('FO', 61.892635, -6.911806),
                                                          ('FR', 46.603354, 1.888334),
                                                          ('GA', -0.803689, 11.609444),
                                                          ('GB', 55.378051, -3.435973),
                                                          ('GD', 12.262776, -61.604171),
                                                          ('GE', 42.315407, 43.356892),
                                                          ('GF', 3.933889, -53.125782),
                                                          ('GG', 49.465691, -2.585278),
                                                          ('GH', 7.946527, -1.023194),
                                                          ('GI', 36.137741, -5.345374),
                                                          ('GL', 71.706936, -42.604303),
                                                          ('GM', 13.443182, -15.310139),
                                                          ('GN', 9.945587, -9.696645),
                                                          ('GP', 16.995971, -62.067641),
                                                          ('GQ', 1.650801, 10.267895),
                                                          ('GR', 39.074208, 21.824312),
                                                          ('GS', -54.429579, -36.587909),
                                                          ('GT', 15.783471, -90.230759),
                                                          ('GU', 13.444304, 144.793731),
                                                          ('GW', 11.803749, -15.180413),
                                                          ('GY', 4.860416, -58.93018),
                                                          ('GZ', 31.354676, 34.308825),
                                                          ('HK', 22.396428, 114.109497),
                                                          ('HM', -53.08181, 73.504158),
                                                          ('HN', 15.199999, -86.241905),
                                                          ('HR', 45.1, 15.2),
                                                          ('HT', 18.971187, -72.285215),
                                                          ('HU', 47.162494, 19.503304),
                                                          ('ID', -0.789275, 113.921327),
                                                          ('IE', 53.41291, -8.24389),
                                                          ('IL', 31.046051, 34.851612),
                                                          ('IM', 54.236107, -4.548056),
                                                          ('IN', 20.593684, 78.96288),
                                                          ('IO', -6.343194, 71.876519),
                                                          ('IQ', 33.223191, 43.679291),
                                                          ('IR', 32.427908, 53.688046),
                                                          ('IS', 64.963051, -19.020835),
                                                          ('IT', 41.87194, 12.56738),
                                                          ('JE', 49.214439, -2.13125),
                                                          ('JM', 18.109581, -77.297508),
                                                          ('JO', 30.585164, 36.238414),
                                                          ('JP', 36.204824, 138.252924),
                                                          ('KE', -0.023559, 37.906193),
                                                          ('KG', 41.20438, 74.766098),
                                                          ('KH', 12.565679, 104.990963),
                                                          ('KI', -3.370417, -168.734039),
                                                          ('KM', -11.875001, 43.872219),
                                                          ('KN', 17.357822, -62.782998),
                                                          ('KP', 40.339852, 127.510093),
                                                          ('KR', 35.907757, 127.766922),
                                                          ('KW', 29.31166, 47.481766),
                                                          ('KY', 19.513469, -80.566956),
                                                          ('KZ', 48.019573, 66.923684),
                                                          ('LA', 19.85627, 102.495496),
                                                          ('LB', 33.854721, 35.862285),
                                                          ('LC', 13.909444, -60.978893),
                                                          ('LI', 47.166, 9.555373),
                                                          ('LK', 7.873054, 80.771797),
                                                          ('LR', 6.428055, -9.429499),
                                                          ('LS', -29.609988, 28.233608),
                                                          ('LT', 55.169438, 23.881275),
                                                          ('LU', 49.815273, 6.129583),
                                                          ('LV', 56.879635, 24.603189),
                                                          ('LY', 26.3351, 17.228331),
                                                          ('MA', 31.791702, -7.09262),
                                                          ('MC', 43.750298, 7.412841),
                                                          ('MD', 47.411631, 28.369885),
                                                          ('ME', 42.708678, 19.37439),
                                                          ('MF', 18.082444, -63.052383),
                                                          ('MG', -18.766947, 46.869107),
                                                          ('MH', 7.131474, 171.184478),
                                                          ('MK', 41.608635, 21.745275),
                                                          ('ML', 17.570692, -3.996166),
                                                          ('MM', 21.913965, 95.956223),
                                                          ('MN', 46.862496, 103.846656),
                                                          ('MO', 22.198745, 113.543873),
                                                          ('MP', 17.33083, 145.38469),
                                                          ('MQ', 14.641528, -61.024174),
                                                          ('MR', 21.00789, -10.940835),
                                                          ('MS', 16.742498, -62.187366),
                                                          ('MT', 35.937496, 14.375416),
                                                          ('MU', -20.348404, 57.552152),
                                                          ('MV', 3.202778, 73.22068),
                                                          ('MW', -13.254308, 34.301525),
                                                          ('MX', 23.634501, -102.552784),
                                                          ('MY', 4.210484, 101.975766),
                                                          ('MZ', -18.665695, 35.529562),
                                                          ('NA', -22.95764, 18.49041),
                                                          ('NC', -20.904305, 165.618042),
                                                          ('NE', 17.607789, 8.081666),
                                                          ('NF', -29.040835, 167.954712),
                                                          ('NG', 9.081999, 8.675277),
                                                          ('NI', 12.865416, -85.207229),
                                                          ('NL', 52.132633, 5.291266),
                                                          ('NO', 60.472024, 8.468946),
                                                          ('NP', 28.394857, 84.124008),
                                                          ('NR', -0.522778, 166.931503),
                                                          ('NU', -19.054445, -169.867233),
                                                          ('NZ', -40.900557, 174.885971),
                                                          ('OM', 21.512583, 55.923255),
                                                          ('PA', 8.537981, -80.782127),
                                                          ('PE', -9.189967, -75.015152),
                                                          ('PF', -17.679742, -149.406843),
                                                          ('PG', -6.314993, 143.95555),
                                                          ('PH', 12.879721, 121.774017),
                                                          ('PK', 30.375321, 69.345116),
                                                          ('PL', 51.919438, 19.145136),
                                                          ('PM', 46.941936, -56.27111),
                                                          ('PN', -24.703615, -127.439308),
                                                          ('PR', 18.220833, -66.590149),
                                                          ('PS', 31.952162, 35.233154),
                                                          ('PT', 39.399872, -8.224454),
                                                          ('PW', 7.51498, 134.58252),
                                                          ('PY', -23.442503, -58.443832),
                                                          ('QA', 25.354826, 51.183884),
                                                          ('RE', -21.115141, 55.536384),
                                                          ('RO', 45.943161, 24.96676),
                                                          ('RS', 44.016521, 21.005859),
                                                          ('RU', 61.52401, 105.318756),
                                                          ('RW', -1.940278, 29.873888),
                                                          ('SA', 23.885942, 45.079162),
                                                          ('SB', -9.64571, 160.156194),
                                                          ('SC', -4.679574, 55.491977),
                                                          ('SD', 12.862807, 30.217636),
                                                          ('SE', 60.128161, 18.643501),
                                                          ('SG', 1.352083, 103.819836),
                                                          ('SH', -24.143474, -10.030696),
                                                          ('SI', 46.151241, 14.995463),
                                                          ('SJ', 77.553604, 23.670272),
                                                          ('SK', 48.669026, 19.699024),
                                                          ('SL', 8.460555, -11.779889),
                                                          ('SM', 43.94236, 12.457777),
                                                          ('SN', 14.497401, -14.452362),
                                                          ('SO', 5.152149, 46.199616),
                                                          ('SR', 3.919305, -56.027783),
                                                          ('SS', 6.876991, 31.306978),
                                                          ('ST', 0.18636, 6.613081),
                                                          ('SV', 13.794185, -88.89653),
                                                          ('SX', 18.04248, -63.05483),
                                                          ('SY', 34.802075, 38.996815),
                                                          ('SZ', -26.522503, 31.465866),
                                                          ('TC', 21.694025, -71.797928),
                                                          ('TD', 15.454166, 18.732207),
                                                          ('TF', -49.280366, 69.348557),
                                                          ('TG', 8.619543, 0.824782),
                                                          ('TH', 15.870032, 100.992541),
                                                          ('TJ', 38.861034, 71.276093),
                                                          ('TK', -8.967363, -171.855881),
                                                          ('TL', -8.874217, 125.727539),
                                                          ('TM', 38.969719, 59.556278),
                                                          ('TN', 33.886917, 9.537499),
                                                          ('TO', -21.178986, -175.198242),
                                                          ('TR', 38.963745, 35.243322),
                                                          ('TT', 10.691803, -61.222503),
                                                          ('TV', -7.109535, 177.64933),
                                                          ('TW', 23.69781, 120.960515),
                                                          ('TZ', -6.369028, 34.888822),
                                                          ('UA', 48.379433, 31.16558),
                                                          ('UG', 1.373333, 32.290275),
                                                          ('UM', 19.280211, 166.647776),
                                                          ('US', 37.09024, -95.712891),
                                                          ('AK', 64.850858, -151.114289),
                                                          ('UY', -32.522779, -55.765835),
                                                          ('UZ', 41.377491, 64.585262),
                                                          ('VA', 41.902916, 12.453389),
                                                          ('VC', 12.984305, -61.287228),
                                                          ('VE', 6.42375, -66.58973),
                                                          ('VG', 18.420695, -64.639968),
                                                          ('VI', 18.335765, -64.896335),
                                                          ('VN', 14.058324, 108.277199),
                                                          ('VU', -15.376706, 166.959158),
                                                          ('WF', -13.768752, -177.156097),
                                                          ('WS', -13.759029, -172.104629),
                                                          ('XK', 42.602636, 20.902977),
                                                          ('YE', 15.552727, 48.516388),
                                                          ('YT', -12.8275, 45.166244),
                                                          ('ZA', -30.559482, 22.937506),
                                                          ('ZM', -13.133897, 27.849332),
                                                          ('ZW', -19.015438, 29.154857);

INSERT INTO dbo.geography (name, predefined, country)
VALUES
    ('Andorra', 1, 1),
    ('United Arab Emirates', 1, 1),
    ('Afghanistan', 1, 1),
    ('Antigua and Barbuda', 1, 1),
    ('Anguilla', 1, 1),
    ('Albania', 1, 1),
    ('Armenia', 1, 1),
    ('Netherlands Antilles', 1, 1),
    ('Anla', 1, 1),
    ('Antarctica', 1, 1),
    ('Argentina', 1, 1),
    ('American Samoa', 1, 1),
    ('Austria', 1, 1),
    ('Australia', 1, 1),
    ('Aruba', 1, 1),
    (N'Åland Islands', 1, 1),
    ('Azerbaijan', 1, 1),
    ('Bosnia and Herzegovina', 1, 1),
    ('Barbados', 1, 1),
    ('Bangladesh', 1, 1),
    ('Belgium', 1, 1),
    ('Burkina Faso', 1, 1),
    ('Bulgaria', 1, 1),
    ('Bahrain', 1, 1),
    ('Burundi', 1, 1),
    ('Benin', 1, 1),
    ('Saint Bartholemy', 1, 1),
    ('Bermuda', 1, 1),
    ('Brunei', 1, 1),
    ('Bolivia', 1, 1),
    ('Bonaire', 1, 1),
    ('Brazil', 1, 1),
    ('Bahamas', 1, 1),
    ('Bhutan', 1, 1),
    ('Bouvet Island', 1, 1),
    ('Botswana', 1, 1),
    ('Belarus', 1, 1),
    ('Belize', 1, 1),
    ('Canada', 1, 1),
    ('Cocos [Keeling] Islands', 1, 1),
    ('Congo [DRC]', 1, 1),
    ('Central African Republic', 1, 1),
    ('Congo [Republic]', 1, 1),
    ('Switzerland', 1, 1),
    (N'Côte d''Ivoire', 1, 1),
    ('Cook Islands', 1, 1),
    ('Chile', 1, 1),
    ('Cameroon', 1, 1),
    ('China', 1, 1),
    ('Colombia', 1, 1),
    ('Costa Rica', 1, 1),
    ('Cuba', 1, 1),
    ('Cape Verde', 1, 1),
    (N'Curaçao', 1, 1),
    ('Christmas Island', 1, 1),
    ('Cyprus', 1, 1),
    ('Czech Republic', 1, 1),
    ('Germany', 1, 1),
    ('Djibouti', 1, 1),
    ('Denmark', 1, 1),
    ('Dominica', 1, 1),
    ('Dominican Republic', 1, 1),
    ('Algeria', 1, 1),
    ('Ecuador', 1, 1),
    ('Estonia', 1, 1),
    ('Egypt', 1, 1),
    ('Western Sahara', 1, 1),
    ('Eritrea', 1, 1),
    ('Spain', 1, 1),
    ('Ethiopia', 1, 1),
    ('Finland', 1, 1),
    ('Fiji', 1, 1),
    ('Falkland Islands [Islas Malvinas]', 1, 1),
    ('Micronesia', 1, 1),
    ('Faroe Islands', 1, 1),
    ('France', 1, 1),
    ('Gabon', 1, 1),
    ('United Kingdom', 1, 1),
    ('Grenada', 1, 1),
    ('Georgia', 1, 1),
    ('French Guiana', 1, 1),
    ('Guernsey', 1, 1),
    ('Ghana', 1, 1),
    ('Gibraltar', 1, 1),
    ('Greenland', 1, 1),
    ('Gambia', 1, 1),
    ('Guinea', 1, 1),
    ('Guadeloupe', 1, 1),
    ('Equatorial Guinea', 1, 1),
    ('Greece', 1, 1),
    ('South Georgia and the South Sandwich Islands', 1, 1),
    ('Guatemala', 1, 1),
    ('Guam', 1, 1),
    ('Guinea-Bissau', 1, 1),
    ('Guyana', 1, 1),
    ('Gaza Strip', 1, 1),
    ('Hong Kong', 1, 1),
    ('Heard Island and McDonald Islands', 1, 1),
    ('Honduras', 1, 1),
    ('Croatia', 1, 1),
    ('Haiti', 1, 1),
    ('Hungary', 1, 1),
    ('Indonesia', 1, 1),
    ('Ireland', 1, 1),
    ('Israel', 1, 1),
    ('Isle of Man', 1, 1),
    ('India', 1, 1),
    ('British Indian Ocean Territory', 1, 1),
    ('Iraq', 1, 1),
    ('Iran', 1, 1),
    ('Iceland', 1, 1),
    ('Italy', 1, 1),
    ('Jersey', 1, 1),
    ('Jamaica', 1, 1),
    ('Jordan', 1, 1),
    ('Japan', 1, 1),
    ('Kenya', 1, 1),
    ('Kyrgyzstan', 1, 1),
    ('Cambodia', 1, 1),
    ('Kiribati', 1, 1),
    ('Comoros', 1, 1),
    ('Saint Kitts and Nevis', 1, 1),
    ('North Korea', 1, 1),
    ('South Korea', 1, 1),
    ('Kuwait', 1, 1),
    ('Cayman Islands', 1, 1),
    ('Kazakhstan', 1, 1),
    ('Laos', 1, 1),
    ('Lebanon', 1, 1),
    ('Saint Lucia', 1, 1),
    ('Liechtenstein', 1, 1),
    ('Sri Lanka', 1, 1),
    ('Liberia', 1, 1),
    ('Lesotho', 1, 1),
    ('Lithuania', 1, 1),
    ('Luxembourg', 1, 1),
    ('Latvia', 1, 1),
    ('Libya', 1, 1),
    ('Morocco', 1, 1),
    ('Monaco', 1, 1),
    ('Moldova', 1, 1),
    ('Montenegro', 1, 1),
    ('Saint Martin', 1, 1),
    ('Madagascar', 1, 1),
    ('Marshall Islands', 1, 1),
    ('Macedonia [FYROM]', 1, 1),
    ('Mali', 1, 1),
    ('Myanmar [Burma]', 1, 1),
    ('Mongolia', 1, 1),
    ('Macau', 1, 1),
    ('Northern Mariana Islands', 1, 1),
    ('Martinique', 1, 1),
    ('Mauritania', 1, 1),
    ('Montserrat', 1, 1),
    ('Malta', 1, 1),
    ('Mauritius', 1, 1),
    ('Maldives', 1, 1),
    ('Malawi', 1, 1),
    ('Mexico', 1, 1),
    ('Malaysia', 1, 1),
    ('Mozambique', 1, 1),
    ('Namibia', 1, 1),
    ('New Caledonia', 1, 1),
    ('Niger', 1, 1),
    ('Norfolk Island', 1, 1),
    ('Nigeria', 1, 1),
    ('Nicaragua', 1, 1),
    ('Netherlands', 1, 1),
    ('Norway', 1, 1),
    ('Nepal', 1, 1),
    ('Nauru', 1, 1),
    ('Niue', 1, 1),
    ('New Zealand', 1, 1),
    ('Oman', 1, 1),
    ('Panama', 1, 1),
    ('Peru', 1, 1),
    ('French Polynesia', 1, 1),
    ('Papua New Guinea', 1, 1),
    ('Philippines', 1, 1),
    ('Pakistan', 1, 1),
    ('Poland', 1, 1),
    ('Saint Pierre and Miquelon', 1, 1),
    ('Pitcairn Islands', 1, 1),
    ('Puerto Rico', 1, 1),
    ('Palestinian Territories', 1, 1),
    ('Portugal', 1, 1),
    ('Palau', 1, 1),
    ('Paraguay', 1, 1),
    ('Qatar', 1, 1),
    ('Réunion', 1, 1),
    ('Romania', 1, 1),
    ('Serbia', 1, 1),
    ('Russia', 1, 1),
    ('Rwanda', 1, 1),
    ('Saudi Arabia', 1, 1),
    ('Solomon Islands', 1, 1),
    ('Seychelles', 1, 1),
    ('Sudan', 1, 1),
    ('Sweden', 1, 1),
    ('Singapore', 1, 1),
    ('Saint Helena', 1, 1),
    ('Slovenia', 1, 1),
    ('Svalbard and Jan Mayen', 1, 1),
    ('Slovakia', 1, 1),
    ('Sierra Leone', 1, 1),
    ('San Marino', 1, 1),
    ('Senegal', 1, 1),
    ('Somalia', 1, 1),
    ('Suriname', 1, 1),
    ('South Sudan', 1, 1),
    ('São Tomé and Príncipe', 1, 1),
    ('El Salvador', 1, 1),
    ('Sint Maarten (Dutch part)', 1, 1),
    ('Syria', 1, 1),
    ('Swaziland', 1, 1),
    ('Turks and Caicos Islands', 1, 1),
    ('Chad', 1, 1),
    ('French Southern Territories', 1, 1),
    ('Togo', 1, 1),
    ('Thailand', 1, 1),
    ('Tajikistan', 1, 1),
    ('Tokelau', 1, 1),
    ('Timor-Leste', 1, 1),
    ('Turkmenistan', 1, 1),
    ('Tunisia', 1, 1),
    ('Tonga', 1, 1),
    ('Turkey', 1, 1),
    ('Trinidad and Tobago', 1, 1),
    ('Tuvalu', 1, 1),
    ('Taiwan', 1, 1),
    ('Tanzania', 1, 1),
    ('Ukraine', 1, 1),
    ('Uganda', 1, 1),
    ('U.S. Minor Outlying Islands', 1, 1),
    ('United States', 1, 1),
    ('United States Alaska', 1, 1),
    ('Uruguay', 1, 1),
    ('Uzbekistan', 1, 1),
    ('Vatican City', 1, 1),
    ('Saint Vincent and the Grenadines', 1, 1),
    ('Venezuela', 1, 1),
    ('British Virgin Islands', 1, 1),
    ('U.S. Virgin Islands', 1, 1),
    ('Vietnam', 1, 1),
    ('Vanuatu', 1, 1),
    ('Wallis and Futuna', 1, 1),
    ('Samoa', 1, 1),
    ('Kosovo', 1, 1),
    ('Yemen', 1, 1),
    ('Mayotte', 1, 1),
    ('South Africa', 1, 1),
    ('Zambia', 1, 1),
    ('Zimbabwe', 1, 1);

INSERT INTO dbo.geography_countries (geography, code) VALUES
                                                          (1, 'AD'), (2, 'AE'), (3, 'AF'), (4, 'AG'), (5, 'AI'), (6, 'AL'), (7, 'AM'), (8, 'AN'), (9, 'AO'), (10, 'AQ'),
                                                          (11, 'AR'), (12, 'AS'), (13, 'AT'), (14, 'AU'), (15, 'AW'), (16, 'AX'), (17, 'AZ'), (18, 'BA'), (19, 'BB'), (20, 'BD'),
                                                          (21, 'BE'), (22, 'BF'), (23, 'BG'), (24, 'BH'), (25, 'BI'), (26, 'BJ'), (27, 'BL'), (28, 'BM'), (29, 'BN'), (30, 'BO'),
                                                          (31, 'BQ'), (32, 'BR'), (33, 'BS'), (34, 'BT'), (35, 'BV'), (36, 'BW'), (37, 'BY'), (38, 'BZ'), (39, 'CA'), (40, 'CC'),
                                                          (41, 'CD'), (42, 'CF'), (43, 'CG'), (44, 'CH'), (45, 'CI'), (46, 'CK'), (47, 'CL'), (48, 'CM'), (49, 'CN'), (50, 'CO'),
                                                          (51, 'CR'), (52, 'CU'), (53, 'CV'), (54, 'CW'), (55, 'CX'), (56, 'CY'), (57, 'CZ'), (58, 'DE'), (59, 'DJ'), (60, 'DK'),
                                                          (61, 'DM'), (62, 'DO'), (63, 'DZ'), (64, 'EC'), (65, 'EE'), (66, 'EG'), (67, 'EH'), (68, 'ER'), (69, 'ES'), (70, 'ET'),
                                                          (71, 'FI'), (72, 'FJ'), (73, 'FK'), (74, 'FM'), (75, 'FO'), (76, 'FR'), (77, 'GA'), (78, 'GB'), (79, 'GD'), (80, 'GE'),
                                                          (81, 'GF'), (82, 'GG'), (83, 'GH'), (84, 'GI'), (85, 'GL'), (86, 'GM'), (87, 'GN'), (88, 'GP'), (89, 'GQ'), (90, 'GR'),
                                                          (91, 'GS'), (92, 'GT'), (93, 'GU'), (94, 'GW'), (95, 'GY'), (96, 'GZ'), (97, 'HK'), (98, 'HM'), (99, 'HN'), (100, 'HR'),
                                                          (101, 'HT'), (102, 'HU'), (103, 'ID'), (104, 'IE'), (105, 'IL'), (106, 'IM'), (107, 'IN'), (108, 'IO'), (109, 'IQ'), (110, 'IR'),
                                                          (111, 'IS'), (112, 'IT'), (113, 'JE'), (114, 'JM'), (115, 'JO'), (116, 'JP'), (117, 'KE'), (118, 'KG'), (119, 'KH'), (120, 'KI'),
                                                          (121, 'KM'), (122, 'KN'), (123, 'KP'), (124, 'KR'), (125, 'KW'), (126, 'KY'), (127, 'KZ'), (128, 'LA'), (129, 'LB'), (130, 'LC'),
                                                          (131, 'LI'), (132, 'LK'), (133, 'LR'), (134, 'LS'), (135, 'LT'), (136, 'LU'), (137, 'LV'), (138, 'LY'), (139, 'MA'), (140, 'MC'),
                                                          (141, 'MD'), (142, 'ME'), (143, 'MF'), (144, 'MG'), (145, 'MH'), (146, 'MK'), (147, 'ML'), (148, 'MM'), (149, 'MN'), (150, 'MO'),
                                                          (151, 'MP'), (152, 'MQ'), (153, 'MR'), (154, 'MS'), (155, 'MT'), (156, 'MU'), (157, 'MV'), (158, 'MW'), (159, 'MX'), (160, 'MY'),
                                                          (161, 'MZ'), (162, 'NA'), (163, 'NC'), (164, 'NE'), (165, 'NF'), (166, 'NG'), (167, 'NI'), (168, 'NL'), (169, 'NO'), (170, 'NP'),
                                                          (171, 'NR'), (172, 'NU'), (173, 'NZ'), (174, 'OM'), (175, 'PA'), (176, 'PE'), (177, 'PF'), (178, 'PG'), (179, 'PH'), (180, 'PK'),
                                                          (181, 'PL'), (182, 'PM'), (183, 'PN'), (184, 'PR'), (185, 'PS'), (186, 'PT'), (187, 'PW'), (188, 'PY'), (189, 'QA'), (190, 'RE'),
                                                          (191, 'RO'), (192, 'RS'), (193, 'RU'), (194, 'RW'), (195, 'SA'), (196, 'SB'), (197, 'SC'), (198, 'SD'), (199, 'SE'), (200, 'SG'),
                                                          (201, 'SH'), (202, 'SI'), (203, 'SJ'), (204, 'SK'), (205, 'SL'), (206, 'SM'), (207, 'SN'), (208, 'SO'), (209, 'SR'), (210, 'SS'),
                                                          (211, 'ST'), (212, 'SV'), (213, 'SX'), (214, 'SY'), (215, 'SZ'), (216, 'TC'), (217, 'TD'), (218, 'TF'), (219, 'TG'), (220, 'TH'),
                                                          (221, 'TJ'), (222, 'TK'), (223, 'TL'), (224, 'TM'), (225, 'TN'), (226, 'TO'), (227, 'TR'), (228, 'TT'), (229, 'TV'), (230, 'TW'),
                                                          (231, 'TZ'), (232, 'UA'), (233, 'UG'), (234, 'UM'), (235, 'US'), (236, 'AK'), (237, 'UY'), (238, 'UZ'), (239, 'VA'), (240, 'VC'),
                                                          (241, 'VE'), (242, 'VG'), (243, 'VI'), (244, 'VN'), (245, 'VU'), (246, 'WF'), (247, 'WS'), (248, 'XK'), (249, 'YE'), (250, 'YT'),
                                                          (251, 'ZA'), (252, 'ZM'), (253, 'ZW');

INSERT INTO dbo.geography (name, predefined)
VALUES ('Africa', 1), -- 254
       ('Northern Africa', 1), -- 255
       ('Western Africa', 1), -- 256
       ('Middle Africa', 1), -- 257
       ('Eastern Africa', 1), -- 258
       ('Southern Africa', 1), -- 259
       ('Americas', 1), -- 260
       ('Americas including Greenland', 1), -- 261
       ('Northern America', 1), -- 262
       ('Caribbean', 1), -- 263
       ('Central America', 1), -- 264
       ('South America', 1), -- 265
       ('APAC', 1), -- 266, Asia Pacific
       ('Asia', 1), -- 267
       ('Central Asia', 1), -- 268
       ('Eastern Asia', 1), -- 269
       ('Southern Asia', 1), -- 270
       ('Southeastern Asia', 1), -- 271
       ('Western Asia', 1), -- 272
       ('APJC', 1), -- 273, Asia Pacific, Japan and China
       ('ANZ', 1), -- 274, Australia and New Zealand only
       ('Benelux', 1), -- 275
       ('BRICS', 1), -- 276, Brazil, Russia, India, China, and South Africa
       ('DACH', 1), -- 277, Germany (Deutschland), Austria, and Switzerland
       ('EMEA', 1), -- 278, Europe, the Middle East, and Africa
       ('European Union', 1), -- 279
       ('Europe', 1), -- 280
       ('Northern Europe', 1), -- 281
       ('Western Europe', 1), -- 282
       ('Eastern Europe', 1), -- 283
       ('Southern Europe', 1), -- 284
       ('Oceania', 1), -- 285
       ('Australia and New Zealand', 1), -- 286 Same as ANZ but with Norfolk Island as well
       ('Melanesia', 1), -- 287
       ('Micronesia', 1), -- 288
       ('Polynesia', 1), -- 289
       ('NORAM', 1); -- 290 North America; US, Canada, and sometimes Mexico

-- Africa
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (254, 'DZ'), (254, 'EG'), (254, 'EH'), (254, 'LY'), (254, 'MA'), (254, 'SD'), (254, 'SS'), (254, 'TN'),
    (254, 'BF'), (254, 'BJ'), (254, 'CI'), (254, 'CV'), (254, 'GH'), (254, 'GM'), (254, 'GN'), (254, 'GW'),
    (254, 'LR'), (254, 'ML'), (254, 'MR'), (254, 'NE'), (254, 'NG'), (254, 'SH'), (254, 'SL'), (254, 'SN'),
    (254, 'TG'), (254, 'AO'), (254, 'CD'), (254, 'CF'), (254, 'CG'), (254, 'CM'), (254, 'GA'), (254, 'GQ'),
    (254, 'ST'), (254, 'TD'), (254, 'BI'), (254, 'DJ'), (254, 'ER'), (254, 'ET'), (254, 'KE'), (254, 'KM'),
    (254, 'MG'), (254, 'MU'), (254, 'MW'), (254, 'MZ'), (254, 'RE'), (254, 'RW'), (254, 'SC'), (254, 'SO'),
    (254, 'TZ'), (254, 'UG'), (254, 'YT'), (254, 'ZM'), (254, 'ZW'), (254, 'BW'), (254, 'LS'), (254, 'NA'),
    (254, 'SZ'), (254, 'ZA');

-- Northern Africa
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (255, 'DZ'), (255, 'EG'), (255, 'EH'), (255, 'LY'), (255, 'MA'), (255, 'SD'), (255, 'SS'), (255, 'TN');

-- Western Africa
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (256, 'BF'), (256, 'BJ'), (256, 'CI'), (256, 'CV'), (256, 'GH'), (256, 'GM'), (256, 'GN'), (256, 'GW'),
    (256, 'LR'), (256, 'ML'), (256, 'MR'), (256, 'NE'), (256, 'NG'), (256, 'SH'), (256, 'SL'), (256, 'SN'),
    (256, 'TG');

-- Middle Africa
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (257, 'AO'), (257, 'CD'), (257, 'CF'), (257, 'CG'), (257, 'CM'), (257, 'GA'), (257, 'GQ'), (257, 'ST'),
    (257, 'TD');

-- Eastern Africa
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (258, 'BI'), (258, 'DJ'), (258, 'ER'), (258, 'ET'), (258, 'KE'), (258, 'KM'), (258, 'MG'), (258, 'MU'),
    (258, 'MW'), (258, 'MZ'), (258, 'RE'), (258, 'RW'), (258, 'SC'), (258, 'SO'), (258, 'TZ'), (258, 'UG'),
    (258, 'YT'), (258, 'ZM'), (258, 'ZW');

-- Southern Africa
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (259, 'BW'), (259, 'LS'), (259, 'NA'), (259, 'SZ'), (259, 'ZA');

-- Americas
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (260, 'BM'), (260, 'CA'), (260, 'PM'), (260, 'US'), (260, 'AG'), (260, 'AI'), (260, 'AW'), (260, 'BB'),
    (260, 'BL'), (260, 'BS'), (260, 'CU'), (260, 'DM'), (260, 'DO'), (260, 'GD'), (260, 'GP'), (260, 'HT'),
    (260, 'JM'), (260, 'KN'), (260, 'KY'), (260, 'LC'), (260, 'MF'), (260, 'MQ'), (260, 'MS'), (260, 'PR'),
    (260, 'TC'), (260, 'TT'), (260, 'VC'), (260, 'VG'), (260, 'VI'), (260, 'BZ'), (260, 'CR'), (260, 'GT'),
    (260, 'HN'), (260, 'MX'), (260, 'NI'), (260, 'PA'), (260, 'SV'), (260, 'AR'), (260, 'BO'), (260, 'BR'),
    (260, 'CL'), (260, 'CO'), (260, 'EC'), (260, 'FK'), (260, 'GF'), (260, 'GY'), (260, 'PE'), (260, 'PY'),
    (260, 'SR'), (260, 'UY'), (260, 'VE');

-- Americas including Greenland
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (261, 'BM'), (261, 'CA'), (261, 'GL'), (261, 'PM'), (261, 'US'), (261, 'AG'), (261, 'AI'), (261, 'AW'),
    (261, 'BB'), (261, 'BL'), (261, 'BS'), (261, 'CU'), (261, 'DM'), (261, 'DO'), (261, 'GD'), (261, 'GP'),
    (261, 'HT'), (261, 'JM'), (261, 'KN'), (261, 'KY'), (261, 'LC'), (261, 'MF'), (261, 'MQ'), (261, 'MS'),
    (261, 'PR'), (261, 'TC'), (261, 'TT'), (261, 'VC'), (261, 'VG'), (261, 'VI'), (261, 'BZ'), (261, 'CR'),
    (261, 'GT'), (261, 'HN'), (261, 'MX'), (261, 'NI'), (261, 'PA'), (261, 'SV'), (261, 'AR'), (261, 'BO'),
    (261, 'BR'), (261, 'CL'), (261, 'CO'), (261, 'EC'), (261, 'FK'), (261, 'GF'), (261, 'GY'), (261, 'PE'),
    (261, 'PY'), (261, 'SR'), (261, 'UY'), (261, 'VE');

-- Northern America
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (262, 'BM'), (262, 'CA'), (262, 'GL'), (262, 'PM'), (262, 'US');

-- Caribbean
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (263, 'AG'), (263, 'AI'), (263, 'AW'), (263, 'BB'), (263, 'BL'), (263, 'BS'), (263, 'CU'), (263, 'DM'),
    (263, 'DO'), (263, 'GD'), (263, 'GP'), (263, 'HT'), (263, 'JM'), (263, 'KN'), (263, 'KY'), (263, 'LC'),
    (263, 'MF'), (263, 'MQ'), (263, 'MS'), (263, 'PR'), (263, 'TC'), (263, 'TT'), (263, 'VC'), (263, 'VG'),
    (263, 'VI');

-- Central America
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (264, 'BZ'), (264, 'CR'), (264, 'GT'), (264, 'HN'), (264, 'MX'), (264, 'NI'), (264, 'PA'), (264, 'SV');

-- South America
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (265, 'AR'), (265, 'BO'), (265, 'BR'), (265, 'CL'), (265, 'CO'), (265, 'EC'), (265, 'FK'), (265, 'GF'),
    (265, 'GY'), (265, 'PE'), (265, 'PY'), (265, 'SR'), (265, 'UY'), (265, 'VE');

-- APAC
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (266, 'AS'), (266, 'AU'), (266, 'BD'), (266, 'BN'), (266, 'BT'), (266, 'CC'), (266, 'CK'), (266, 'CN'),
    (266, 'CX'), (266, 'FJ'), (266, 'FM'), (266, 'GU'), (266, 'HK'), (266, 'ID'), (266, 'IN'), (266, 'IO'),
    (266, 'JP'), (266, 'KH'), (266, 'KI'), (266, 'KP'), (266, 'KR'), (266, 'LA'), (266, 'LK'), (266, 'MH'),
    (266, 'MM'), (266, 'MN'), (266, 'MO'), (266, 'MP'), (266, 'MV'), (266, 'MY'), (266, 'NC'), (266, 'NF'),
    (266, 'NP'), (266, 'NR'), (266, 'NU'), (266, 'NZ'), (266, 'PF'), (266, 'PG'), (266, 'PH'), (266, 'PK'),
    (266, 'PN'), (266, 'PW'), (266, 'SB'), (266, 'SG'), (266, 'TH'), (266, 'TK'), (266, 'TL'), (266, 'TO'),
    (266, 'TV'), (266, 'TW'), (266, 'VN'), (266, 'VU'), (266, 'WF'), (266, 'WS');

-- Asia
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (267, 'TM'), (267, 'TJ'), (267, 'KG'), (267, 'KZ'), (267, 'UZ'), (267, 'CN'), (267, 'HK'), (267, 'JP'),
    (267, 'KP'), (267, 'KR'), (267, 'MN'), (267, 'MO'), (267, 'TW'), (267, 'AF'), (267, 'BD'), (267, 'BT'),
    (267, 'IN'), (267, 'IR'), (267, 'LK'), (267, 'MV'), (267, 'NP'), (267, 'PK'), (267, 'BN'), (267, 'ID'),
    (267, 'KH'), (267, 'LA'), (267, 'MM'), (267, 'MY'), (267, 'PH'), (267, 'SG'), (267, 'TH'), (267, 'TL'),
    (267, 'VN'), (267, 'AE'), (267, 'AM'), (267, 'AZ'), (267, 'BH'), (267, 'CY'), (267, 'GE'), (267, 'IL'),
    (267, 'IQ'), (267, 'JO'), (267, 'KW'), (267, 'LB'), (267, 'OM'), (267, 'PS'), (267, 'QA'), (267, 'SA'),
    (267, 'SY'), (267, 'TR'), (267, 'YE');

-- Central Asia
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (268, 'TM'), (268, 'TJ'), (268, 'KG'), (268, 'KZ'), (268, 'UZ');

-- Eastern Asia
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (269, 'CN'), (269, 'HK'), (269, 'JP'), (269, 'KP'), (269, 'KR'), (269, 'MN'), (269, 'MO'), (269, 'TW');

-- Southern Asia
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (270, 'AF'), (270, 'BD'), (270, 'BT'), (270, 'IN'), (270, 'IR'), (270, 'LK'), (270, 'MV'), (270, 'NP'),
    (270, 'PK');

-- Southeastern Asia
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (271, 'BN'), (271, 'ID'), (271, 'KH'), (271, 'LA'), (271, 'MM'), (271, 'MY'), (271, 'PH'), (271, 'SG'),
    (271, 'TH'), (271, 'TL'), (271, 'VN');

-- Western Asia
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (272, 'AE'), (272, 'AM'), (272, 'AZ'), (272, 'BH'), (272, 'CY'), (272, 'GE'), (272, 'IL'), (272, 'IQ'),
    (272, 'JO'), (272, 'KW'), (272, 'LB'), (272, 'OM'), (272, 'PS'), (272, 'QA'), (272, 'SA'), (272, 'SY'),
    (272, 'TR'), (272, 'YE');

-- APJC
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (273, 'AS'), (273, 'AU'), (273, 'BD'), (273, 'BN'), (273, 'BT'), (273, 'CC'), (273, 'CK'), (273, 'CN'),
    (273, 'CX'), (273, 'FJ'), (273, 'FM'), (273, 'GU'), (273, 'HK'), (273, 'HM'), (273, 'ID'), (273, 'IN'),
    (273, 'IO'), (273, 'JP'), (273, 'KH'), (273, 'KI'), (273, 'KP'), (273, 'KR'), (273, 'LA'), (273, 'LK'),
    (273, 'MH'), (273, 'MM'), (273, 'MN'), (273, 'MO'), (273, 'MP'), (273, 'MV'), (273, 'MY'), (273, 'NC'),
    (273, 'NF'), (273, 'NP'), (273, 'NR'), (273, 'NU'), (273, 'NZ'), (273, 'PF'), (273, 'PG'), (273, 'PH'),
    (273, 'PK'), (273, 'PN'), (273, 'PW'), (273, 'SB'), (273, 'SG'), (273, 'TH'), (273, 'TK'), (273, 'TL'),
    (273, 'TO'), (273, 'TV'), (273, 'TW'), (273, 'VN'), (273, 'VU'), (273, 'WS');

-- ANZ
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (274, 'AU'), (274, 'NZ');

-- Benelux
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (275, 'BE'), (275, 'NL'), (275, 'LU');

-- BRICS
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (276, 'RU'), (276, 'BR'), (276, 'CN'), (276, 'IN'), (276, 'ZA');

-- DACH
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (277, 'DE'), (277, 'AT'), (277, 'CH');

-- EMEA
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (278, 'AF'), (278, 'AX'), (278, 'AL'), (278, 'DZ'), (278, 'AD'), (278, 'AO'), (278, 'AM'), (278, 'AT'),
    (278, 'AZ'), (278, 'BH'), (278, 'BY'), (278, 'BE'), (278, 'BJ'), (278, 'BA'), (278, 'BW'), (278, 'BV'),
    (278, 'BG'), (278, 'BF'), (278, 'BI'), (278, 'CM'), (278, 'CV'), (278, 'CF'), (278, 'TD'), (278, 'KM'),
    (278, 'CD'), (278, 'CG'), (278, 'HR'), (278, 'CY'), (278, 'CZ'), (278, 'DK'), (278, 'DJ'), (278, 'EG'),
    (278, 'GQ'), (278, 'ER'), (278, 'EE'), (278, 'ET'), (278, 'FK'), (278, 'FO'), (278, 'FI'), (278, 'FR'),
    (278, 'GA'), (278, 'GM'), (278, 'GE'), (278, 'DE'), (278, 'GH'), (278, 'GI'), (278, 'GR'), (278, 'GL'),
    (278, 'GG'), (278, 'GW'), (278, 'HU'), (278, 'IS'), (278, 'IR'), (278, 'IQ'), (278, 'IE'), (278, 'IM'),
    (278, 'IL'), (278, 'IT'), (278, 'CI'), (278, 'JE'), (278, 'JO'), (278, 'KZ'), (278, 'KE'), (278, 'XK'),
    (278, 'KW'), (278, 'KG'), (278, 'LV'), (278, 'LB'), (278, 'LS'), (278, 'LR'), (278, 'LY'), (278, 'LI'),
    (278, 'LT'), (278, 'LU'), (278, 'MK'), (278, 'MG'), (278, 'MW'), (278, 'ML'), (278, 'MT'), (278, 'MR'),
    (278, 'MU'), (278, 'YT'), (278, 'MD'), (278, 'MC'), (278, 'ME'), (278, 'MA'), (278, 'MZ'), (278, 'NA'),
    (278, 'NL'), (278, 'NE'), (278, 'NG'), (278, 'NO'), (278, 'OM'), (278, 'PK'), (278, 'PS'), (278, 'PL'),
    (278, 'PT'), (278, 'QA'), (278, 'RE'), (278, 'RO'), (278, 'RU'), (278, 'RW'), (278, 'SH'), (278, 'SM'),
    (278, 'ST'), (278, 'SA'), (278, 'SN'), (278, 'RS'), (278, 'SC'), (278, 'SL'), (278, 'SK'), (278, 'SI'),
    (278, 'SO'), (278, 'ZA'), (278, 'GS'), (278, 'ES'), (278, 'SD'), (278, 'SJ'), (278, 'SZ'), (278, 'SE'),
    (278, 'CH'), (278, 'SY'), (278, 'TJ'), (278, 'TZ'), (278, 'TG'), (278, 'TN'), (278, 'TR'), (278, 'TM'),
    (278, 'UG'), (278, 'UA'), (278, 'AE'), (278, 'GB'), (278, 'UZ'), (278, 'VA'), (278, 'EH'), (278, 'YE'),
    (278, 'ZM'), (278, 'ZW');

-- European Union
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (279, 'AT'), (279, 'BE'), (279, 'BG'), (279, 'HR'), (279, 'CY'), (279, 'CZ'), (279, 'DK'), (279, 'EE'),
    (279, 'FI'), (279, 'FR'), (279, 'DE'), (279, 'GR'), (279, 'HU'), (279, 'IE'), (279, 'IT'), (279, 'LV'),
    (279, 'LT'), (279, 'LU'), (279, 'MT'), (279, 'NL'), (279, 'PL'), (279, 'PT'), (279, 'RO'), (279, 'SK'),
    (279, 'SI'), (279, 'ES'), (279, 'SE');

-- Europe
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (280, 'GG'), (280, 'JE'), (280, 'AX'), (280, 'DK'), (280, 'EE'), (280, 'FI'), (280, 'FO'), (280, 'GB'),
    (280, 'IE'), (280, 'IM'), (280, 'IS'), (280, 'LT'), (280, 'LV'), (280, 'NO'), (280, 'SE'), (280, 'SJ'),
    (280, 'AT'), (280, 'BE'), (280, 'CH'), (280, 'DE'), (280, 'FR'), (280, 'LI'), (280, 'LU'), (280, 'MC'),
    (280, 'NL'), (280, 'BG'), (280, 'BY'), (280, 'CZ'), (280, 'HU'), (280, 'MD'), (280, 'PL'), (280, 'RO'),
    (280, 'RU'), (280, 'SK'), (280, 'UA'), (280, 'AD'), (280, 'AL'), (280, 'BA'), (280, 'ES'), (280, 'GI'),
    (280, 'GR'), (280, 'HR'), (280, 'IT'), (280, 'ME'), (280, 'MK'), (280, 'MT'), (280, 'RS'), (280, 'PT'),
    (280, 'SI'), (280, 'SM'), (280, 'VA');

-- Northern Europe
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (281, 'GG'), (281, 'JE'), (281, 'AX'), (281, 'DK'), (281, 'EE'), (281, 'FI'), (281, 'FO'), (281, 'GB'),
    (281, 'IE'), (281, 'IM'), (281, 'IS'), (281, 'LT'), (281, 'LV'), (281, 'NO'), (281, 'SE'), (281, 'SJ');

-- Western Europe
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (282, 'AT'), (282, 'BE'), (282, 'CH'), (282, 'DE'), (282, 'FR'), (282, 'LI'), (282, 'LU'), (282, 'MC'),
    (282, 'NL');

-- Eastern Europe
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (283, 'BG'), (283, 'BY'), (283, 'CZ'), (283, 'HU'), (283, 'MD'), (283, 'PL'), (283, 'RO'), (283, 'RU'),
    (283, 'SK'), (283, 'UA');

-- Southern Europe
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (284, 'AD'), (284, 'AL'), (284, 'BA'), (284, 'ES'), (284, 'GI'), (284, 'GR'), (284, 'HR'), (284, 'IT'),
    (284, 'ME'), (284, 'MK'), (284, 'MT'), (284, 'RS'), (284, 'PT'), (284, 'SI'), (284, 'SM'), (284, 'VA');

-- Oceania
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (285, 'AU'), (285, 'NF'), (285, 'NZ'), (285, 'FJ'), (285, 'NC'), (285, 'PG'), (285, 'SB'), (285, 'VU'),
    (285, 'FM'), (285, 'GU'), (285, 'KI'), (285, 'MH'), (285, 'MP'), (285, 'NR'), (285, 'PW'), (285, 'AS'),
    (285, 'CK'), (285, 'NU'), (285, 'PF'), (285, 'PN'), (285, 'TK'), (285, 'TO'), (285, 'TV'), (285, 'WF'),
    (285, 'WS');

-- Australia and New Zealand
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (286, 'AU'), (286, 'NF'), (286, 'NZ');

-- Melanesia
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (287, 'FJ'), (287, 'NC'), (287, 'PG'), (287, 'SB'), (287, 'VU');

-- Micronesia
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (288, 'FM'), (288, 'GU'), (288, 'KI'), (288, 'MH'), (288, 'MP'), (288, 'NR'), (288, 'PW');

-- Polynesia
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (289, 'AS'), (289, 'CK'), (289, 'NU'), (289, 'PF'), (289, 'PN'), (289, 'TK'), (289, 'TO'), (289, 'TV'),
    (289, 'WF'), (289, 'WS');

-- NORAM
INSERT INTO dbo.geography_countries (geography, code)
VALUES
    (290, 'US'), (290, 'CA'), (290, 'MX'), (290, 'GT'), (290, 'BZ'), (290, 'CU'), (290, 'DO'), (290, 'HT'),
    (290, 'HN'), (290, 'SV'), (290, 'NI'), (290, 'CR'), (290, 'PA');

INSERT INTO dbo.Teams (id, name, markup, gross_margin, day_rate, hourly_rate, total_allocated_cost, total_allocated_hours)
VALUES
    ('937b379c-c326-4db2-9575-8313f59ddf2c', 'Low cost team', 10.00, 20.00, 208.00, 26.00, 160000, 6240),
    ('67e1e024-e589-4637-af32-e9ebc0303c9c', 'Medium cost team', 25.00, 40.00, 384.00, 48.00, 300000, 6240),
    ('eba9685d-9621-48a2-ab7f-ed708e4dd63d', 'High cost team', 20.00, 35.00, 536.00, 67.00, 420000, 6240);

INSERT INTO dbo.Profiles (
    profile_id,
    name,
    currency,
    country_id,
    resource_type,
    is_archived,
    annual_cost,
    effectiveness,
    annual_hours,
    effective_work_hours,
    hours_per_day
)
VALUES
    ('24c2b2f8-47f0-4c8b-b55d-cb49996feb44', 'Cheap profile 1', 'EUR', 39, FALSE, FALSE, 40000.0000, 80, 2080.00, 1664, 8.00), -- IT consultant in North America
    ('44656c14-5882-4247-a0eb-7dc9253ec923', 'Cheap profile 2', 'EUR', 13, FALSE, FALSE, 40000.0000, 80, 2080.00, 1664, 8.00), -- Project manager in Europe
    ('071495d6-cfce-46f2-a6fd-5a77ca4fbdea', 'Cheap profile 3', 'EUR', 107, FALSE, FALSE, 40000.0000, 80, 2080.00, 1664, 8.00), -- Developer in Asia
    ('869877f5-b85c-4abb-890a-8de561c82641', 'Cheap manager', 'EUR', 261, TRUE, FALSE, 40000.0000, 80, 0.00, 0, 8.00), -- Support specialist in Latin America
    ('a2f4d5d8-bd00-4f6d-a3d9-51cf4463d91e', 'Medium profile 1', 'EUR', 2, FALSE, FALSE, 80000.0000, 80, 2080.00, 1664, 8.00), -- Security analyst in the Middle East
    ('5910ffa5-6ef2-44d6-9d89-a7d642810536', 'Medium profile 2', 'EUR', 39, FALSE, FALSE, 80000.0000, 80, 2080.00, 1664, 8.00), -- Part-time executive in Canada
    ('0a2d65d5-e379-4145-a153-a4f509954b65', 'Medium profile 3', 'EUR', 13, FALSE, FALSE, 80000.0000, 80, 2080.00, 1664, 8.00), -- Full-time consultant in Germany
    ('317f43ef-569c-4a1b-b881-edad7eb79db7', 'High profile 1', 'EUR', 107, FALSE, FALSE, 120000.0000, 80, 2080.00, 1664, 8.00), -- Part-time developer in India
    ('92ac4d45-af92-475c-acb1-af2c626d86b0', 'High profile 2', 'EUR', 78, FALSE, FALSE, 120000.0000, 80, 2080.00, 1664, 8.00), -- Part-time project manager in the UK
    ('2b485563-7c7f-49e7-b58f-b1b9fd2bbadf', 'High profile 3', 'EUR', 14, FALSE, FALSE, 120000.0000, 80, 2080.00, 1664, 8.00), -- Overtime specialist in Australia
    ('50b601e3-dd42-422b-ac43-1aae17dff125', 'High medium manager', 'EUR', 32, True, FALSE, 120000.0000, 80, 0.00, 0, 8.00); -- Standard worker in Brazil


INSERT INTO dbo.Teams_profiles (teamId, profileId, cost_allocation, hour_allocation, allocated_cost_on_team, allocated_hours_on_team, day_rate_on_team)
VALUES
    ('937b379c-c326-4db2-9575-8313f59ddf2c', '24c2b2f8-47f0-4c8b-b55d-cb49996feb44', 100.00, 100.00, 40000.00 , 2080, 152),  -- IT consultant mainly in North American Operations Center
    ('937b379c-c326-4db2-9575-8313f59ddf2c', '44656c14-5882-4247-a0eb-7dc9253ec923', 100.00, 100.00, 40000.00 , 2080, 152),  -- IT consultant also part of Product Leadership Circle

    ('937b379c-c326-4db2-9575-8313f59ddf2c', '071495d6-cfce-46f2-a6fd-5a77ca4fbdea', 100.00, 100.00, 40000.00 , 2080, 152),  -- Project manager primarily in EMEA Market Growth Team
    ('937b379c-c326-4db2-9575-8313f59ddf2c', '869877f5-b85c-4abb-890a-8de561c82641', 100.00, 100.00, 40000.00 , 0, 0), -- Project manager also working in Corporate Strategic Initiatives

    ('67e1e024-e589-4637-af32-e9ebc0303c9c', 'a2f4d5d8-bd00-4f6d-a3d9-51cf4463d91e', 100.00, 100.00, 80000.00, 2080, 304),  -- Developer largely involved in APAC Product Innovation Center
    ('67e1e024-e589-4637-af32-e9ebc0303c9c', '5910ffa5-6ef2-44d6-9d89-a7d642810536', 100.00, 100.00, 80000.00, 2080, 304), -- Developer also contributing to Innovation and R&D Center

    ('67e1e024-e589-4637-af32-e9ebc0303c9c', '0a2d65d5-e379-4145-a153-a4f509954b65', 100.00, 100.00, 80000.00, 2080, 304), -- Security analyst fully dedicated to Global Cybersecurity Command

    ('67e1e024-e589-4637-af32-e9ebc0303c9c', '50b601e3-dd42-422b-ac43-1aae17dff125', 50.00, 50.00, 60000.00, 0, 0), -- Support specialist fully dedicated to Latin American Customer Excellence

    ('eba9685d-9621-48a2-ab7f-ed708e4dd63d', '317f43ef-569c-4a1b-b881-edad7eb79db7', 100.00, 100.00, 120000.00, 2080, 464), -- Overtime specialist fully in European Solutions Engineering

    ('eba9685d-9621-48a2-ab7f-ed708e4dd63d', '92ac4d45-af92-475c-acb1-af2c626d86b0', 100.00, 100.00, 120000.00, 2080, 464),  -- Full-time consultant mostly in Asian Market Development Group
    ('eba9685d-9621-48a2-ab7f-ed708e4dd63d', '2b485563-7c7f-49e7-b58f-b1b9fd2bbadf', 100.00, 100.00, 120000.00, 2080, 464), -- Full-time consultant also supporting Client Solutions Design Team

    ('eba9685d-9621-48a2-ab7f-ed708e4dd63d', '50b601e3-dd42-422b-ac43-1aae17dff125', 50.00, 50.00, 60000.00, 0, 0); -- Flex-time engineer fully in Global Cloud Infrastructure Solutions


INSERT INTO dbo.Currency (currency_code, eur_conversion_rate, usd_conversion_rate)
VALUES ('USD', 1.08, 1.00), ('EUR', 1.00, 0.93);

INSERT INTO dbo.Teams_profiles_history (
    team_id,
    profile_id,
    profile_history_id,
    reason,
    hourly_rate,
    day_rate,
    annual_cost,
    annual_hours,
    cost_allocation,
    hour_allocation,
    profile_hourly_rate,
    profile_day_rate,
    profile_annual_cost,
    profile_annual_hours,
    updated_at
)
VALUES
    ('937b379c-c326-4db2-9575-8313f59ddf2c', '24c2b2f8-47f0-4c8b-b55d-cb49996feb44', null, 'TEAM_CREATED', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '2024-05-15 22:54:50.373'),
    ('937b379c-c326-4db2-9575-8313f59ddf2c', '44656c14-5882-4247-a0eb-7dc9253ec923', null,'TEAM_CREATED', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '2024-05-15 22:54:50.373'),
    ('937b379c-c326-4db2-9575-8313f59ddf2c', '071495d6-cfce-46f2-a6fd-5a77ca4fbdea', null,'TEAM_CREATED', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '2024-05-16 22:54:50.373'),
    ('937b379c-c326-4db2-9575-8313f59ddf2c', '869877f5-b85c-4abb-890a-8de561c82641', null,'TEAM_CREATED', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '2024-05-16 22:54:50.373'),
    ('67e1e024-e589-4637-af32-e9ebc0303c9c', 'a2f4d5d8-bd00-4f6d-a3d9-51cf4463d91e', null,'TEAM_CREATED', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '2024-05-17 22:54:50.373'),
    ('67e1e024-e589-4637-af32-e9ebc0303c9c', '5910ffa5-6ef2-44d6-9d89-a7d642810536', null, 'TEAM_CREATED', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '2024-05-17 22:54:50.373'),
    ('67e1e024-e589-4637-af32-e9ebc0303c9c', '0a2d65d5-e379-4145-a153-a4f509954b65', null, 'TEAM_CREATED', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '2024-05-18 22:54:50.373'),
    ('67e1e024-e589-4637-af32-e9ebc0303c9c', '50b601e3-dd42-422b-ac43-1aae17dff125', null, 'TEAM_CREATED', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '2024-05-18 22:54:50.373'),
    ('eba9685d-9621-48a2-ab7f-ed708e4dd63d', '317f43ef-569c-4a1b-b881-edad7eb79db7', null, 'TEAM_CREATED', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '2024-05-19 22:54:50.373'),
    ('eba9685d-9621-48a2-ab7f-ed708e4dd63d', '92ac4d45-af92-475c-acb1-af2c626d86b0', null, 'TEAM_CREATED', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '2024-05-19 22:54:50.373'),
    ('eba9685d-9621-48a2-ab7f-ed708e4dd63d', '2b485563-7c7f-49e7-b58f-b1b9fd2bbadf', null, 'TEAM_CREATED', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '2024-05-20 22:54:50.373'),
    ('eba9685d-9621-48a2-ab7f-ed708e4dd63d', '50b601e3-dd42-422b-ac43-1aae17dff125', null, 'TEAM_CREATED', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '2024-05-20 22:54:50.373');
