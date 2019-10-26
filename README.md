# odens-eye

Goals:
1) From a page display all games in csv
2) From a page display games in csv from particular day

Base from sbt new denisftw/play-event-sourcing-starter.g8.
Data from https://www.basketball-reference.com/leagues/NBA_2020_games.html

## Commands to run first time
sudo psql -U postgres --command "CREATE USER scalauser WITH SUPERUSER PASSWORD 'scalapass';"
sudo createdb -U postgres -O scalauser authdb
sudo createdb -U postgres -O scalauser statsstoredb
npm i

## Commands to run web app
npm run watch
sbt run
