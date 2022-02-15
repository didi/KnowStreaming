ARG NODE_VERSION=12.20.0
ARG NGINX_VERSION=1.21.5-alpine
FROM node:${NODE_VERSION} AS builder
ARG OUTPUT_PATH=dist

ENV TZ Asia/Shanghai
WORKDIR /opt
COPY . .
RUN npm config set registry https://registry.npm.taobao.org \
    && npm install \
    # Change the output directory to dist
    && sed -i "s#../kafka-manager-web/src/main/resources/templates#$OUTPUT_PATH#g" webpack.config.js \
    && npm run prod-build

FROM nginx:${NGINX_VERSION}

ENV TZ=Asia/Shanghai

COPY --from=builder /opt/dist /opt/dist
COPY --from=builder /opt/web.conf /etc/nginx/conf.d/default.conf
