package util
import java.text.SimpleDateFormat

object Common {
    val currentYear = "2020"

    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val brWebDataFormat = new SimpleDateFormat("E, MMM dd, yyyy")

    val teamToTm = Map(
      "Atlanta Hawks" -> "ATL",
      "Boston Celtics" -> "BOS",
      "Brooklyn Nets" -> "NJN",
      "Charlotte Hornets" -> "CHA",
      "Chicago Bulls" -> "CHI",
      "Cleveland Cavaliers" -> "CLE",
      "Dallas Mavericks" -> "DAL",
      "Denver Nuggets" -> "DEN",
      "Detroit Pistons" -> "DET",
      "Golden State Warriors" -> "GSW",
      "Houston Rockets" -> "HOU",
      "Indiana Pacers" -> "IND",
      "Los Angeles Clippers" -> "LAC",
      "Los Angeles Lakers" -> "LAL",
      "Memphis Grizzlies" -> "MEM",
      "Miami Heat" -> "MIA",
      "Milwaukee Bucks" -> "MIL",
      "Minnesota Timberwolves" -> "MIN",
      "New Orleans Pelicans" -> "NOH",
      "New York Knicks" -> "NYK",
      "Oklahoma City Thunder" -> "OKC",
      "Orlando Magic" -> "ORL",
      "Philadelphia 76ers" -> "PHI",
      "Phoenix Suns" -> "PHO",
      "Portland Trail Blazers" -> "POR",
      "Sacramento Kings" -> "SAC",
      "San Antonio Spurs" -> "SAS",
      "Toronto Raptors" -> "TOR",
      "Utah Jazz" -> "UTA",
      "Washington Wizards" -> "WAS"
    )

    val monthNumToStr = Map(
      1 -> "january",
      2 -> "february",
      3 -> "march",
      4 -> "april",
      5 -> "may",
      6 -> "june",
      7 -> "july",
      8 -> "august",
      9 -> "september",
      10 -> "october",
      11 -> "november",
      12 -> "december"
    )
  }
