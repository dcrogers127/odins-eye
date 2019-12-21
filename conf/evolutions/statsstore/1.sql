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
    pt_spread int, -- away_pts - home_pts
    ot varchar(250),
    notes varchar(250),
    box_score_url varchar(250)
);

CREATE TABLE game_summary_stats (
    team_date_id varchar UNIQUE PRIMARY KEY,  -- concatenation of game_date and tm
    game_date date not null,
    team varchar(250) not null,
    tm varchar(3) not null,
    n_games int not null,
    wins int not null,
    losses int not null,
    points_per_game int not null,
    opponents_points_per_game int not null,
    margin_of_victory float not null,
    strength_of_schedule float not null,
    simple_rating_system float not null,
    pace float not null,
    net_rating not null -- point differential per 100 possessions
);

# --- !Downs

DROP TABLE games;

DROP EXTENSION "uuid-ossp";
