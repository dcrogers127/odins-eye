import React from 'react';
import axios from 'axios';
import { makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';

class GameManager extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      games: []
    };
  };

  const useStyles = makeStyles({
    root: {
      width: '100%',
      overflowX: 'auto',
    },
    table: {
      minWidth: 650,
    },
  });

  handleResponse = (response) => {
    if (response.status == 200) {
      this.setState({
        games: response.data
      })
    } else {
      console.error(response.statusText);
    }
  }

  componentDidMount=()=>{
    axios.get("/api/games").then(this.handleResponse);
  };
}

export default GameManager;
