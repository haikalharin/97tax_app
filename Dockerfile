FROM maven:3.6.3-openjdk-11
ARG profile=default
ENV TZ=America/New_York

# e-File related sources
RUN mkdir -p /mef_client/ack
ADD ./backend/lib/mef_client /mef_client
ENV A2A_TOOLKIT_HOME="/mef_client"

ADD . /97tax
WORKDIR /97tax

# Run Maven build
RUN cd backend/lib \
    && mkdir -p /home/ubuntu/mailroom_batches \
    && chown root:root /home/ubuntu/mailroom_batches \
    && chmod 755 /home/ubuntu/mailroom_batches \
    && chmod 755 /97tax/backend/lib/install.sh \
    && mkdir -p /home/ubuntu/server_logs \
    && chown root:root /home/ubuntu/server_logs \
    && chmod 755 /home/ubuntu/server_logs \
    && ./install.sh \
    && cd ../../ \
    && mvn clean package -DskipTests=true -P $profile

EXPOSE 8088

ENTRYPOINT [ "sh", "-c", "java -Dserver.port=8088 -Dspring.datasource.url=${DATA_SOURCE_URL} -Dspring.datasource.username=${DATA_SOURCE_USERNAME} -Dspring.datasource.password=${DATA_SOURCE_PASSWORD} -Dcardinal.cruise.api.identifier=${CRUISE_API_ID} -Dcardinal.cruise.org.unit.id=${CRUISE_ORG_UNIT_ID} -Dcardinal.cruise.api.key=${CRUISE_API_KEY} -Demail.smtp.auth.key=${SMTP_KEY} -Demail.smtp.auth.secret=${SMTP_SECRET} -Dshipengine.api.key=${SHIPENGINE_API_KEY} -Dshipengine.label.carrier.id=${SHIPENGINE_LABEL_CARRIER_ID} -Daws.accessKey=${AWS_ACCESS_KEY} -Daws.secretKey=${AWS_SECRET_KEY} -Dorbital.username=${ORBITAL_USERNAME} -Dorbital.password=${ORBITAL_PASSWORD} -Dorbital.merchantId=${ORBITAL_MERCHANTID} -Dsignatureapi.apikey=${SIGNATUREAPI_API_KEY} -Djava.security.egd=file:/dev/./urandom -jar /97tax/backend/target/backend-0.0.1-SNAPSHOT.jar"]
