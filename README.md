# odins-eye

Base from sbt new denisftw/play-event-sourcing-starter.g8.

## Current Plan
1. Add actions
    1. add_game
    2. add_score
    3. compute_game_stats
    4. add_lines 
1. Update scores for existing games into DB
    - could be an event per games or just one event
2. Create process to daily check for new scores and update.
3. Spread info
    - Get historic spread info
    - Get current spread info
    - hold for now. get some good stats

We want to log any change to main database to the events table
Actual state of database will be in statsstore

1. Deploy to aws beanstalk? using docker
    - try sample apps first.  Add in dependencies

### Commands to run first time
sudo psql -U postgres --command "CREATE USER scalauser WITH SUPERUSER PASSWORD 'scalapass';"

sudo createdb -U postgres -O scalauser authdb

sudo createdb -U postgres -O scalauser eventstoredb

sudo createdb -U postgres -O scalauser statsstoredb

npm i

// sudo dropdb -U postgres statsstoredb


### Commands to run web app
./bin/zookeeper-server-start.sh config/zookeeper.properties

./bin/kafka-server-start.sh config/server.properties

npm run watch

sbt run

### Generate key
sbt

playGenerateSecret

### Notes
Rendering table with npm package material-ui (see https://material-ui.com)

Sports API for line info:
- https://oddsapi.io/
    - doesn't look good
- https://jsonodds.com/
    - only provides one number
- https://therundown.io/
    - looks interesting
- https://sportradar.us/sports-data/
    - ???
- https://sportsdata.io/live-odds-api
    - Have to email to get any info
