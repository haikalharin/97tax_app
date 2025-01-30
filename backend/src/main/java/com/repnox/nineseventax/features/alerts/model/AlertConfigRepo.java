package com.repnox.nineseventax.features.alerts.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AlertConfigRepo extends CrudRepository<AlertConfig, Long> {

    String REFUND_MANUAL_CONFIG = "notifications.refund.manual";
    String REFUND_MANUAL_SECONDARY_CONFIG = "notifications.refund.manual.secondary";
    String EIN_FULFILLMENT_AUTO = "ein.fulfillment.auto"; // We will need only enabled field here
    String EIN_EXTENSION_REDEPLOY = "ein.extension.redeploy"; // We will need only enabled field here
    String EIN_SERVICE_MAINTENANCE = "ein.service.maintenance"; // We will need email field here, which will be used to store the timestamp

    @Query("select a from AlertConfig a where a.name = ?1")
    AlertConfig findByName(String name);

}
