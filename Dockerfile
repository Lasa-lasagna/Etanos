FROM postgres:18-alpine3.24

ENV POSTGRES_USER=amargo
ENV POSTGRES_PASSWORD=amagaret
ENV POSTGRES_DB=gas

VOLUME ["/var/lib/postgresql"]

EXPOSE 5432
