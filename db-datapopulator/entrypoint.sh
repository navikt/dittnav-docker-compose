#!/bin/sh

DB_USER="${DB_NAME}-user"
psql postgresql://$DB_USER:$DB_PASSWORD@$DB_HOST/$DB_NAME -f ./testdata.sql;
