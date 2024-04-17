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
GENERATED_MEMBER=$(generate_member_id)

SQL="INSERT INTO $TABLE_NAME (id, email, enabled, name, password, provider, role) VALUES ('$GENERATED_MEMBER','admin@admin.pl', true, 'Admin', '$GENERATED_PASSWORD', 'LOCAL', 'ROLE_ADMIN');"
docker exec -i $CONTAINER_NAME psql -U $DB_USER_NAME -d $DB_NAME -c "$SQL"

echo "Try to create user: $GENERATED_MEMBER"
