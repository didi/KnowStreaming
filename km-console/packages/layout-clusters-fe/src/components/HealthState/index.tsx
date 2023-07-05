import React from 'react';
import GoodState from '@src/assets/health-good.png';
import MediumState from '@src/assets/health-medium.png';
import PoorState from '@src/assets/health-poor.png';
import DownState from '@src/assets/health-down.png';
import UnknownState from '@src/assets/health-unknown.png';
import GoodStateEmoji from '@src/assets/health-good-emoji.png';
import MediumStateEmoji from '@src/assets/health-medium-emoji.png';
import PoorStateEmoji from '@src/assets/health-poor-emoji.png';
import DownStateEmoji from '@src/assets/health-down-emoji.png';
import './index.less';

export enum HealthStateEnum {
  UNKNOWN = -1,
  GOOD,
  MEDIUM,
  POOR,
  DOWN,
}

interface HealthStateProps {
  state: HealthStateEnum;
  width: string | number;
  height: string | number;
}

const HEALTH_STATE_MAP = {
  [HealthStateEnum.GOOD]: GoodState,
  [HealthStateEnum.MEDIUM]: MediumState,
  [HealthStateEnum.POOR]: PoorState,
  [HealthStateEnum.DOWN]: DownState,
  [HealthStateEnum.UNKNOWN]: UnknownState,
};

const HEALTH_STATE_EMOJI_MAP = {
  [HealthStateEnum.GOOD]: GoodStateEmoji,
  [HealthStateEnum.MEDIUM]: MediumStateEmoji,
  [HealthStateEnum.POOR]: PoorStateEmoji,
  [HealthStateEnum.DOWN]: DownStateEmoji,
  [HealthStateEnum.UNKNOWN]: DownStateEmoji,
};

const HEALTH_STATE_DESC_MAP = {
  [HealthStateEnum.GOOD]: '状态优异',
  [HealthStateEnum.MEDIUM]: '状态良好',
  [HealthStateEnum.POOR]: '状态较差',
  [HealthStateEnum.DOWN]: '状态异常',
  [HealthStateEnum.UNKNOWN]: '状态异常',
};

export const getHealthStateEmoji = (state: HealthStateEnum, width = 16, height = 16) => {
  return (
    <img
      width={width}
      height={height}
      style={{ marginTop: -3 }}
      src={HEALTH_STATE_EMOJI_MAP[state] || HEALTH_STATE_EMOJI_MAP[HealthStateEnum.UNKNOWN]}
    />
  );
};

export const getHealthStateDesc = (state: HealthStateEnum) => {
  return HEALTH_STATE_DESC_MAP[state] || HEALTH_STATE_DESC_MAP[HealthStateEnum.UNKNOWN];
};

const HealthState = (props: HealthStateProps) => {
  const { state, width, height } = props;

  return (
    <div className="health-state" style={{ width, height }}>
      <img src={HEALTH_STATE_MAP[state] || UnknownState} />
    </div>
  );
};

export default HealthState;
