# odins-eye

Base from sbt new denisftw/play-event-sourcing-starter.g8.

## Current Plan
1. Improve logging
    - We want to log any change to main database to the events table
    - Actual state of database will be in statsstore
2. Add team stats
    - MOV - Margin of victory
    - SOS - Strength of Schedule
    - SRS - Simple Rating System
3. Deploy to aws beanstalk? using docker
    - try sample apps first.  Add in dependencies
4. Spread info
    - Get historic spread info
    - Get current spread info
    - hold for now. get some good stats


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
