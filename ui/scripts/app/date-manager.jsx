import React from 'react';
import DatePicker from "react-datepicker";

import "react-datepicker/dist/react-datepicker.css";
// import 'react-datepicker/dist/react-datepicker-cssmodules.css';

class DateManager extends React.Component {
  state = {
    startDate: new Date()
  };

  handleChange = startDate => {
    this.setState({
      startDate: startDate
    });
  };

  render = () => {
    return <div className="date-manager">
      <DatePicker
        selected={this.state.startDate}
        onChange={this.handleChange}
      />
    </div>;
  }
}

export default DateManager;
