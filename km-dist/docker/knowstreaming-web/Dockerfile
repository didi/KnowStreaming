FROM docker.io/library/nginx:1.23.1

LABEL org.opencontainers.image.authors="haoqi123"

WORKDIR /pub
EXPOSE 80

ENV HTML_FILE=./pub
ENV NGINX_CONF=./conf/*.conf

COPY ${NGINX_CONF} /etc/nginx/conf.d/default.conf
COPY ${HTML_FILE} /pub

HEALTHCHECK --interval=60s --timeout=60s CMD curl -f http://localhost/ks-km/api/v3/open/health || exit 1