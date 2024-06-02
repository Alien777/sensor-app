#!/bin/bash

CONTAINER_NAME=sensor-postgres-docker-compose
DB_NAME=postgres
DB_USER_NAME=postgres
TABLE_NAME=member
CHARACTERS="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
LENGTH=16

generate_member_id() {
    local result=""
    for (( i=0; i<LENGTH; i++ )); do
        local rand_index=$(($RANDOM % ${#CHARACTERS}))
        result="${result}${CHARACTERS:$rand_index:1}"
    done
    echo "$result"
}

GENERATED_PASSWORD=$(htpasswd -bnBC 10 "" admin | tr -d ':\n')

SQL="UPDATE $TABLE_NAME SET password = '$GENERATED_PASSWORD' WHERE email = 'admin@admin.pl';"
docker exec -i $CONTAINER_NAME psql -U $DB_USER_NAME -d $DB_NAME -c "$SQL"

echo "Updated password for user: admin@admin.pl"
