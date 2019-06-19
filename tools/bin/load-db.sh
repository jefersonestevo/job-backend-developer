#!/bin/bash

cd `dirname $0`

echo ""
echo "Loading database scripts"
echo ""

docker cp ../db/create-db.sql postgres:/tmp
docker cp ../db/populate-db.sql postgres:/tmp

docker exec postgres /bin/bash -c "
psql -U postgres -d postgres -a -c \"CREATE DATABASE user_info;\"
psql -U postgres -d user_info -a -f /tmp/create-db.sql
psql -U postgres -d user_info -a -f /tmp/populate-db.sql

psql -U postgres -d postgres -a -c \"CREATE DATABASE user_info_test;\"
psql -U postgres -d user_info_test -a -f /tmp/create-db.sql

exit"

echo ""
echo "Load database scripts finished"
echo ""
