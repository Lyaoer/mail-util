FROM java

MAINTAINER liyao li.yao@founder.com.cn

EXPOSE  9025

COPY mail-util-1.0.0-SNAPSHOT.jar /opt/mail_util.jar
COPY mail-service.sh /opt/
RUN ls /opt
RUN chmod +x /opt/mail-service.sh
WORKDIR /opt

ENTRYPOINT /opt/mail-service.sh