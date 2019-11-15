# --- !Ups

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE games (
    game_id varchar UNIQUE PRIMARY KEY,  -- concatenation of game_date, visitor, and home
    game_date date not null,
    start_time varchar,
    visitor varchar(250) not null,
    visitor_pts int,
    home varchar(250) not null,
    home_pts int,
    ot varchar(250) not null
);

# --- !Downs

DROP TABLE games;

DROP EXTENSION "uuid-ossp";
