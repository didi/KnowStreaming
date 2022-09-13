FROM docker.io/library/nginx:1.23.1
WORKDIR /pub
EXPOSE 80
COPY ./conf/*.conf /etc/nginx/conf.d/default.conf
COPY ./pub /pub
# HEALTHCHECK --interval=30s --timeout=30s CMD curl -f http://localhost/ks-km/api/v3/open/health || exit 1