# --- !Ups

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE games
(
    id SERIAL UNIQUE PRIMARY KEY,
    game_date varchar(250) not null,
    start_et varchar(250) not null,
    visitor varchar(250) not null,
    visitor_pts varchar(250) not null,
    home varchar(250) not null,
    home_pts varchar(250) not null,
    box_score_url varchar(250) not null,
    ot varchar(250) not null,
    attend varchar(250) not null,
    notes varchar(250) not null
);

# --- !Downs

DROP TABLE games;

DROP EXTENSION "uuid-ossp";
