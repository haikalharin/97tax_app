#!/usr/bin/env bash
mvn install:install-file -Dfile=CentinelClient.jar -DpomFile=pom-CentinelClient.xml
mvn install:install-file -Dfile=anet-java-sdk-1.9.7.jar -DpomFile=pom-anet-java-sdk-1.9.7.xml
mvn install:install-file -Dfile=PaymentechSDK.jar -DpomFile=pom-paymentTech.xml
mvn install:install-file -Dfile=mef_client_sdk.jar -DpomFile=pom-mef_client_sdk.xml