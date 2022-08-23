import { Button, Space } from 'knowdesign';
import React, { useLayoutEffect, useState } from 'react';
import Joyride, { Step, TooltipRenderProps } from 'react-joyride';
import './index.less';

interface TourGuideProps {
  run: boolean;
  guide: {
    key: string;
    steps: Step[];
  };
}

// 全部配置项参考: https://github.com/gilbarbara/react-joyride/blob/3e08384415a831b20ce21c8423b6c271ad419fbf/src/styles.js
const joyrideCommonStyle = {
  options: {
    zIndex: 2000,
  },
  spotlight: {
    borderRadius: 12,
  },
};

const JoyrideTooltip = (props: TooltipRenderProps) => {
  const { continuous, index, size, step, isLastStep, skipProps, primaryProps, tooltipProps } = props;

  const isTop = step.placement.startsWith('top');
  const isRight = step.placement.startsWith('right');
  const isBottom = step.placement.startsWith('bottom');
  const tooltipStyle = {
    [`margin-${isTop ? 'bottom' : isRight ? 'left' : isBottom ? 'top' : 'right'}`]: 20,
    [`margin-${isTop || isBottom ? 'left' : 'top'}`]: -10,
  };
  const arrowStyle = {
    [isTop || isBottom ? 'left' : 'top']: 24,
    [isTop ? 'bottom' : isRight ? 'left' : isBottom ? 'top' : 'right']: -24,
    transform: `rotate(${isTop ? -90 : isRight ? 0 : isBottom ? 90 : 180}deg)`,
  };

  return (
    <div className="joyride-tooltip" {...tooltipProps} style={tooltipStyle}>
      {step.title && <div className="joyride-tooltip-header">{step.title}</div>}
      <div className="joyride-tooltip-body">{step.content}</div>
      <div className="joyride-tooltip-footer">
        <div className="joyride-tooltip-footer-left">
          {index + 1} / {size}
        </div>
        <div className="joyride-tooltip-footer-right">
          {/* {index > 0 && (
            <Button {...backProps} size="small">
              上一个
            </Button>
          )} */}
          <Space>
            <Button {...skipProps} size="small" type="text">
              跳过
            </Button>
            {continuous && (
              <Button {...primaryProps} size="small" type="primary">
                {isLastStep ? '我知道了' : '下一个'}
              </Button>
            )}
          </Space>
        </div>
      </div>
      <div className="joyride-tooltip-arrow" style={arrowStyle}>
        <div className="joyride-tooltip-arrow-line"></div>
      </div>
    </div>
  );
};

const TourGuide = ({ guide, run: ready }: TourGuideProps) => {
  const [run, setRun] = useState<boolean>(false);

  useLayoutEffect(() => {
    if (ready) {
      const curGuideKey = guide.key;
      const guidedStorage = localStorage.getItem('guided');
      let guidedInfo: string[];

      try {
        guidedInfo = JSON.parse(guidedStorage) || [];
        if (!guidedInfo.includes(curGuideKey)) {
          guidedInfo.push(curGuideKey);
          localStorage.setItem('guided', JSON.stringify(guidedInfo));
          setRun(true);
        }
      } catch (err) {
        err;
      }
    }
  }, [ready]);

  return (
    <Joyride
      steps={guide.steps}
      run={run}
      continuous
      hideCloseButton
      showProgress
      disableCloseOnEsc
      disableOverlayClose
      disableScrolling
      disableScrollParentFix
      tooltipComponent={JoyrideTooltip}
      styles={joyrideCommonStyle}
      floaterProps={{
        hideArrow: true,
        offset: 60,
      }}
    />
  );
};

export * from './steps';
export default TourGuide;
