FROM alpine

COPY entrypoint.sh /entrypoint.sh
COPY testdata.sql /testdata.sql

RUN apk add --no-cache postgresql-client

ENTRYPOINT ["/entrypoint.sh"]

