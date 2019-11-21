import React from 'react';
import axios from 'axios';

import { makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';

const useStyles = makeStyles({
  root: {
    width: '100%',
    overflowX: 'auto',
  },
  table: {
    minWidth: 650,
  },
});

class GameManager extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      games: []
    };
  };

  handleResponse = (response) => {
    console.log(response);
    if (response.status == 200) {
      this.setState({
        games: response.data
      })
    } else {
      console.error(response.statusText);
    }
  };

  componentDidMount = () => {
    axios.get("/api/games").then(this.handleResponse);
  };

  render = () => {
    return <div className="game-manager">
      <Paper className={"root"}>
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
    </div>;
  }
}

export default GameManager;
