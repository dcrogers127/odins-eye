import React from 'react';
import DateManager from './date-manager.jsx';

class DateRangeManager extends React.Component {
  state = {
    startDate: new Date(),
    endDate: new Date()
  };

  handleStartChange = startDate => {
    this.setState({
      startDate: startDate
    });
  };

  handleEndChange = endDate => {
    this.setState({
      startDate: startDate
    });
  };

  render = () => {
    return <div>
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
              <DatePicker
                selected={this.state.startDate}
                onChange={this.handleStartChange}
              />
            </td>
              <DatePicker
                selected={this.state.endDate}
                onChange={this.handleEndChange}
              />
            <td>
            </td>
          </tr>
        </tbody>
      </table>
    </div>;
  }
}

export default DateManager;
