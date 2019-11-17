# odins-eye

Goals:
1. Abstract InMemoryDao\Actor to generalize events
    1. Add actions
        1. add_game
        2. add_score
        3. compute_game_stats
        4. add_lines 
2. From a page display games in csv from particular day
    1. Add calendar with date range picker at top.  Defualt to today
    and one day before\after
3. Page with all actions logged with most recent at top.

Base from sbt new denisftw/play-event-sourcing-starter.g8.
Data from https://www.basketball-reference.com/leagues/NBA_2020_games.html

## Current Plan
0. Create CSV with all games, some scores
1. Load CSV into staging table
2. Load games into games table
    - could be many events or just one event
    - let's make it one event
3. Update scores for existing games into DB
    - could be an event per games or just one event
4. Create process to daily check for new scores and update.

We want to log any change to main database to the events table
Actual state of database will be in statsstore


### Commands to run first time
sudo psql -U postgres --command "CREATE USER scalauser WITH SUPERUSER PASSWORD 'scalapass';"

sudo createdb -U postgres -O scalauser authdb

sudo createdb -U postgres -O scalauser eventstoredb

sudo createdb -U postgres -O scalauser statsstoredb

npm i

sudo dropdb -U postgres statsstoredb


### Commands to run web app
./bin/zookeeper-server-start.sh config/zookeeper.properties
./bin/kafka-server-start.sh config/server.properties
npm run watch
sbt run


### Notes
Rendering table with npm package material-ui (see https://material-ui.com)


