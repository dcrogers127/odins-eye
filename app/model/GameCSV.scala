package model

case class GameCSV (
  game_date: String,
  start_et: String,
  visitor: String,
  visitor_pts: String,
  home: String,
  home_pts: String,
  box_score_url: String,
  ot: String,
  attend: String,
  notes: String
)
