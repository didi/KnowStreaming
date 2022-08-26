import { Progress, Table, TableProps, Tooltip, ProTable } from 'knowdesign';
import * as React from 'react';
import { useIntl } from 'react-intl';
import './style/result.less';

interface ICard {
  label: string;
  value: number;
  key: string;
  unit?: string;
}

interface ITableCol {
  partition: number;
  offset: number;
  time?: string;
  timestamp: string;
  key?: number;
  value?: number;
  header?: string;
}

interface IProcess {
  label: string;
  totalNumber: number;
  currentNumber: number;
  key: string;
}

interface IProps {
  cardList?: ICard[];
  showCardList: boolean;
  processList?: IProcess[];
  processTitle?: React.ReactNode | string;
  showProcessList: boolean;
  tableTitle?: React.ReactNode;
  tableProps: any;
}

const getValueWithUnit = (value: number) => {
  if (value === undefined || value === null) return '-';

  if (value < 10000) {
    return Number.isInteger(value) ? value : value.toFixed(2) + '';
  }
  // 9999万
  if (value < 99990000) {
    return (value / 10000).toFixed(2) + '万';
  }

  if (value < 999999990000) {
    return (value / 10000 / 10000).toFixed(2) + '亿';
  }

  return (value / (10000 * 10000 * 1000)).toFixed(2) + '千亿';
};

const TestResult = (props: IProps): JSX.Element => {
  const intl = useIntl();
  const { cardList, showCardList, processList, showProcessList, processTitle, tableTitle, tableProps } = props;

  return (
    <>
      <div className="test-result-panel">
        <div className="page-title">{intl.formatMessage({ id: 'test.result' })}</div>
        {showCardList && (
          <div className="card-panel">
            {cardList?.map((item, index) => (
              <div className="card-item" key={`card-${index}`}>
                <h4 className="label">{item.label}</h4>
                <div className="value">
                  {item.value ?? '-'}
                  <span className="unit">{item.unit}</span>
                </div>
              </div>
            ))}
          </div>
        )}
        {showProcessList && processList.length > 0 && (
          <div className="process-panel">
            {
              <>
                <div className="title">{processTitle}</div>
                {processList?.map((item, index) => (
                  <div className="process-item" key={`process-${index}`}>
                    <Tooltip title={`${item.currentNumber}/${item.totalNumber}`}>
                      <div className="content">
                        <span className="label">{item.label}</span>
                        <span className="info">{`${getValueWithUnit(item.currentNumber)}/${getValueWithUnit(item.totalNumber)}`}</span>
                      </div>
                      <Progress
                        className="value"
                        percent={item.totalNumber ? +((item.currentNumber / item.totalNumber) * 100) : 0}
                        showInfo={false}
                        trailColor={'#ECECF1'}
                        strokeColor={'#556EE6'}
                      />
                    </Tooltip>
                  </div>
                ))}
              </>
            }
          </div>
        )}
        <div className="table-panel">
          {tableTitle ? <div className="table-title">{tableTitle}</div> : null}
          <ProTable tableProps={tableProps} />
        </div>
      </div>
    </>
  );
};

export default TestResult;
