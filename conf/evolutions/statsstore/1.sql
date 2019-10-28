# --- !Ups

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE games
(
    id SERIAL UNIQUE PRIMARY KEY,
    game_date VARCHAR(250) NOT NULL,
    start_et VARCHAR(250) NOT NULL,
    visitor VARCHAR(250) NOT NULL,
    visitor_pts VARCHAR(250) NOT NULL,
    home VARCHAR(250) NOT NULL,
    home_pts VARCHAR(250) NOT NULL,
    box_score_url VARCHAR(250) NOT NULL,
    ot VARCHAR(250) NOT NULL,
    Attend VARCHAR(250) NOT NULL,
    Notes VARCHAR(250) NOT NULL
);

COPY games FROM 'public/data/path_to_csv_file.csv' DELIMITERS ',' CSV;

# --- !Downs

DROP TABLE games;

DROP EXTENSION "uuid-ossp";
