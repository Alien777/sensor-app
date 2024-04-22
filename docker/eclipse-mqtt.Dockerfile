FROM eclipse-mosquitto:latest

COPY docker/data/config/mqtt/mosquitto.conf /mosquitto/config/mosquitto.conf
COPY docker/data/config/mqtt/aclfile /mosquitto/config/aclfile
COPY docker/data/config/mqtt/password_file /mosquitto/config/password_file

RUN chmod 644 /mosquitto/config/aclfile /mosquitto/config/password_file

CMD ["/usr/sbin/mosquitto", "-c", "/mosquitto/config/mosquitto.conf"]