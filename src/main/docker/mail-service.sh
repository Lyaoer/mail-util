#/bin/sh
java -jar /opt/mail_util.jar --spring.profiles.active=${MAIL_RABBITMQ:-development}