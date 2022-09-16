FROM docker.io/amd64/nginx:1.22

LABEL maintainer="haoqi123 <1148648445@qq.com>"

WORKDIR /knowstreaming

ARG VERSION_NODE=14.20.0
ARG VERSION_KM=3.0.0-beta.2

# SHELL ["/bin/bash", "-o", "pipefail", "-c"]

RUN apt update \
  && apt install -y git unzip

RUN echo 'server { \
        listen 80; \
        server_name localhost; \
        gzip on; \
        gzip_buffers        16 8k; \
        gzip_comp_level     4; \
        gzip_http_version   1.0; \
        gzip_min_length     1280; \
        gzip_types          text/plain text/css text/xml application/x-javascript application/xml application/xml+rss application/json application/javascript text/*; \
        gzip_vary           on; \
        root /pub; \
        location / { \
            root /pub; \
            if ($request_filename ~* .*\.(?:htm|html|json)$) { \
                add_header Cache-Control "private, no-store, no-cache, must-revalidate, proxy-revalidate"; \
            } \
            try_files $uri /layout/index.html; \
        } \
        location ~* \.(json)$ { \
            add_header Cache-Control no-cache; \
        } \
        location @kmfallback { \
        } \
        #location ~ ^/(clusters|config|cluster|login) { \
        #    rewrite ^.*$ /; \
        #} \
         location ~ ^/ks-km/api/v3 { \
            #rewrite ^/ks-km/api/v3/(.*)$ /ks-km/ks-km/api/v3/$1 break; \
            proxy_pass http://knowstreaming-manager; \
            #proxy_pass https://api-kylin-xg02.intra.xiaojukeji.com; \
            #proxy_cookie_path  /ks-km/ /; \
            #proxy_set_header Host $host; \
            #proxy_set_header Referer $http_referer; \
            #proxy_set_header Cookie $http_cookie; \
            #proxy_set_header X-Real-Ip $remote_addr; \
            #proxy_set_header  X-Forwarded-For  $proxy_add_x_forwarded_for; \
        } \
        location ~ ^/logi-security/api/v1 { \
            #rewrite ^/logi-security/api/v1/(.*)$ /ks-km/logi-security/api/v1/$1 break; \
            proxy_pass http://knowstreaming-manager; \
            #proxy_pass https://api-kylin-xg02.intra.xiaojukeji.com; \
        } \
        location ~ ^/(401|403|404|500){ \
             rewrite ^.*$ /; \
        } \
    }' > /etc/nginx/conf.d/default.conf

RUN curl -fsSLO --compressed "https://npm.taobao.org/mirrors/node/v${VERSION_NODE}/node-v${VERSION_NODE}-linux-x64.tar.gz" \
  && mkdir node/ \
  && tar -zxf "node-v${VERSION_NODE}-linux-x64.tar.gz" -C node/ --strip-components=1 --no-same-owner \
  && rm -rf /knowstreaming/*.tar.gz \
  && ln -s /knowstreaming/node/bin/node /usr/local/bin/node \
  && ln -s /knowstreaming/node/bin/npm /usr/local/bin/npm \
  && npm config set strict-ssl false \
  && npm config set registry https://registry.npm.taobao.org \
  && npm info underscore \
  && node --version \
  && npm --version \
  && curl -fsSLO --compressed https://github.com/didi/KnowStreaming/archive/refs/tags/v${VERSION_KM}.zip \
  && unzip -o -q v${VERSION_KM}.zip \
  && cd KnowStreaming-${VERSION_KM}/km-console \
  && npm install -g lerna && npm run i && npm run build \
  && mv /knowstreaming/KnowStreaming-${VERSION_KM}/km-rest/src/main/resources/templates /pub \
  && npm uninstall -g lerna \
  && cd / \
  && rm -rf /knowstreaming/* /usr/local/bin/node /usr/local/bin/npm \
  && apt remove -y git unzip && apt autoremove -y && apt remove --purge --auto-remove -y \
  && rm -rf /var/lib/apt/lists/* /tmp /root/.npm

#ENTRYPOINT ["ls", "/ks_ui/km-console"]

#CMD ["nginx", "-g", "daemon off;"]
