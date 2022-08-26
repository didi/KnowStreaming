


- [Requirements](#requirements)
- [Installing](#installing)





## Requirements

* Kubernetes >= 1.14
* [Helm][] >= 2.17.0


## Installing

* 默认配置为全部安装（elasticsearch + mysql + knowstreaming）；
* 如果使用已有的elasticsearch(7.6.x) 和 mysql(5.7) 只需调整 values.yaml 部分参数即可;
* Install it:
  - with Helm 3: `helm install knowstreaming knowstreaming-manager/`