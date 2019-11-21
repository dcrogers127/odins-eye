# --- !Ups

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE games (
    game_id varchar UNIQUE PRIMARY KEY,  -- concatenation of game_date, away_tm, and home_tm
    game_date date not null,
    start_time varchar,
    away_team varchar(250) not null,
    away_tm varchar(3) not null,
    home_team varchar(250) not null,
    home_tm varchar(250) not null,
    away_pts int,
    home_pts int,
    ot varchar(250),
    notes varchar(250),
    box_score_url varchar(250)
);

# --- !Downs

DROP TABLE games;

DROP EXTENSION "uuid-ossp";
