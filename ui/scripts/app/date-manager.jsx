import React from 'react';
import DatePicker from "react-datepicker";

import "react-datepicker/dist/react-datepicker.css";
// import 'react-datepicker/dist/react-datepicker-cssmodules.css';

class DateManager extends React.Component {
  state = {
    startDate: new Date(),
    endDate: new Date()
  };

  handleChange = date => {
    this.setState({
      startDate: date,
      endDate: date
    });
  };

  render = () => {
    return <div className="date-manager">
      <table>
        <thead>
          <tr>
            <th>Start Date</th>
            <th>End Date</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>
              <div>
                <DatePicker
                  selected={this.state.startDate}
                  onChange={this.handleChange}
                />
              </div>
            </td>
              <div>
                <DatePicker
                  selected={this.state.endDate}
                  onChange={this.handleChange}
                />
              </div>
            <td>
            </td>
          </tr>
        </tbody>
      </table>
    </div>;
  }
}

export default DateManager;
