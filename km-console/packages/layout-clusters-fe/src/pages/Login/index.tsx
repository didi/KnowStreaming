import React, { useEffect, useState } from 'react';
import { LOGIN_MENU, LOGIN_MENU_MAP } from './config';
import './index.less';
import { AppContainer, Carousel } from 'knowdesign';
import Logo from '@src/assets/ks-logo.png';
import egOneTitle from './img/eg1-title.png';
import egOneContent from './img/eg1-content.png';
import egTwoContent from './img/eg2-content.png';

const carouselList = [
  <div key="2">
    <div className="carousel-eg-ctr carousel-eg-ctr-two">
      <img className="carousel-eg-ctr-two-img img-one" src={egTwoContent} />
      <div className="carousel-eg-ctr-two-desc desc-one">
        <span>Github: </span>
        <span>6.8K</span>
        <span>+ Star的的实时流处理平台</span>
      </div>
      <div className="carousel-eg-ctr-two-desc desc-two">
        从开源至今社区内已经超过 2000+ 用户使用，从新创公司到巨头，尤其是得到各行业一线企业开发者的信赖。
      </div>
    </div>
  </div>,
  // <div key="1">
  //   <div className="carousel-eg-ctr carousel-eg-ctr-one">
  //     <img className="carousel-eg-ctr-one-img img-one" src={egOneTitle} />
  //     <div className="carousel-eg-ctr-one-desc desc-one">可能是北半球最简单易用的 Kafka 管控平台</div>
  //     <img className="carousel-eg-ctr-one-img img-two" src={egOneContent} />
  //   </div>
  // </div>,
];

export const Login: React.FC<any> = () => {
  const [global, setGlobal] = AppContainer.useGlobalValue();
  const [selectedKeys, setSelectedKeys] = useState([LOGIN_MENU[0].key]);

  const renderContent = () => {
    return LOGIN_MENU_MAP.get(selectedKeys[0])?.render(handleMenuClick) || LOGIN_MENU_MAP.get(LOGIN_MENU[0].key)?.render(handleMenuClick);
  };

  const handleMenuClick = (e: string) => {
    setSelectedKeys([e]);
    window.location.hash = e;
  };

  // 跳转到登录页时，清空全局状态
  useEffect(() => {
    if (Object.keys(global).length) {
      setGlobal({});
    }
  }, [global]);

  return (
    <div className="login-page">
      <div className="login-page-left">
        <Carousel autoplay={true} autoplaySpeed={5000}>
          {carouselList}
        </Carousel>
      </div>
      <div className="login-page-right">
        <div className="login-page-right-content">
          <div className="login-page-right-content-title">
            <img className="logo" src={Logo} />
            <div className="desc">可能是北半球最简单易用的 Kafka 管控平台</div>
          </div>
          <div className="login-page-right-content-content">{renderContent()}</div>
        </div>
      </div>
    </div>
  );
};
