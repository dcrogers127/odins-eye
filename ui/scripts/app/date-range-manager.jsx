import React from 'react';
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";


class DateRange extends React.Component {
  constructor(props) {
    super(props);
  }

  render = () => {
    return <div className="date-range-manager">
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
                selected={this.props.startDate}
                onChange={this.props.onStartChange}
                selectsStart
                startDate={this.props.startDate}
                endDate={this.props.endDate}
              />
            </td>
              <DatePicker
                selected={this.props.endDate}
                onChange={this.props.onEndChange}
                selectsEnd
                endDate={this.props.endDate}
                minDate={this.props.startDate}
              />
            <td>
            </td>
          </tr>
        </tbody>
      </table>
    </div>;
  }
}

export default DateRange;
