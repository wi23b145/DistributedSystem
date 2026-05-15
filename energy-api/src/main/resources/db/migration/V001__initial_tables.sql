CREATE TABLE IF NOT EXISTS usage_data (
                                          hour TIMESTAMP PRIMARY KEY,
                                          community_produced DOUBLE PRECISION NOT NULL,
                                          community_used DOUBLE PRECISION NOT NULL,
                                          grid_used DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS percentage_data (
                                               hour TIMESTAMP PRIMARY KEY,
                                               community_depleted DOUBLE PRECISION NOT NULL,
                                               grid_portion DOUBLE PRECISION NOT NULL
);