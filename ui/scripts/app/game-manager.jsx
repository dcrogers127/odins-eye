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

function createData(name, calories, fat, carbs, protein) {
  return { name, calories, fat, carbs, protein };
}

const rows = [
  createData('Frozen yoghurt', 159, 6.0, 24, 4.0),
  createData('Ice cream sandwich', 237, 9.0, 37, 4.3),
  createData('Eclair', 262, 16.0, 24, 6.0),
  createData('Cupcake', 305, 3.7, 67, 4.3),
  createData('Gingerbread', 356, 16.0, 49, 3.9),
  createData('Susie cake', 356, 16.0, 49, 3.9),
];

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
              <TableCell align="right">Visitor</TableCell>
              <TableCell align="right">Visitor PTS</TableCell>
              <TableCell align="right">Home</TableCell>
              <TableCell align="right">Home PTS</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.games.map(row => (
              <TableRow key={row.id}>
                <TableCell component="th" scope="row">
                  {row.game_date}
                </TableCell>
                <TableCell align="right">{row.visitor}</TableCell>
                <TableCell align="right">{row.visitor_pts}</TableCell>
                <TableCell align="right">{row.home}</TableCell>
                <TableCell align="right">{row.home_pts}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
    </div>;
  }
}

export default GameManager;
