FROM mysql:latest
LABEL MAINTAINER="Rodrigo"
COPY proposta-db.sql /docker-entrypoint-initdb.d
EXPOSE 3306