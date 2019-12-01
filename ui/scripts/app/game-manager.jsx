import React from 'react';
import axios from 'axios';

import { makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';

import DateRange from './date-range-manager.jsx';

const useStyles = makeStyles({
  root: {
    width: '100%',
    overflowX: 'auto',
  },
  table: {
    minWidth: 650,
  },
});

const dayDuration = 1000 * 3600 * 24;
const initStartDate = new Date();

class GameManager extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      games: [],
      startDate: initStartDate,
      endDate: new Date(initStartDate.getTime() + 2 * dayDuration)
    };
  };

  handleResponse = (response) => {
    if (response.status == 200) {
      this.setState({
        games: response.data
      })
    } else {
      console.error(response.statusText);
    }
  };

  componentDidMount = () => {
    axios.get("/api/games", {
      params: {
        startDate: this.state.startDate,
        endDate: this.state.endDate
      }
    })
    .then(this.handleResponse);
  };

  onStartChange = (date) => {
    this.setState({startDate: date});
    axios.get("/api/games", {
      params: {
        startDate: date,
        endDate: this.state.endDate
      }
    })
    .then(this.handleResponse);
  };

  onEndChange = (date) => {
    this.setState({endDate: date});
    axios.get("/api/games", {
      params: {
        startDate: this.state.startDate,
        endDate: date
      }
    })
    .then(this.handleResponse);
  };

  render = () => {
    return <div className="game-manager">
      <DateRange
        startDate={this.state.startDate}
        endDate={this.state.endDate}
        onStartChange={this.onStartChange}
        onEndChange={this.onEndChange}
      />
      <div>
        <Paper className={"paper"}>
          <Table className={"table"} aria-label="simple table">
            <TableHead>
              <TableRow>
                <TableCell>Date</TableCell>
                <TableCell align="right">Away</TableCell>
                <TableCell align="right">Away Points</TableCell>
                <TableCell align="right">Home</TableCell>
                <TableCell align="right">Home Points</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {this.state.games.map(row => (
                <TableRow key={row.gameId}>
                  <TableCell component="th" scope="row">
                    {row.gameDate}
                  </TableCell>
                  <TableCell align="right">{row.awayTeam}</TableCell>
                  <TableCell align="right">{row.awayPoints}</TableCell>
                  <TableCell align="right">{row.homeTeam}</TableCell>
                  <TableCell align="right">{row.homePoints}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      </div>
    </div>;
  }
}

export default GameManager;
