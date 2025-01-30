package com.repnox.nineseventax.features.alerts;

import com.repnox.nineseventax.exceptions.NotFoundException;
import com.repnox.nineseventax.features.alerts.model.AlertConfig;
import com.repnox.nineseventax.features.alerts.model.AlertConfigRepo;
import com.repnox.nineseventax.features.alerts.model.RefundAlert;
import com.repnox.nineseventax.features.auth.AuthRole;
import com.repnox.nineseventax.features.auth.AuthService;
import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.utils.DefaultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertsController {

    private static final Logger LOG = LoggerFactory.getLogger(AlertsController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private AlertConfigRepo alertConfigRepo;

    @Autowired
    private MailjetSender mailjetSender;

    // TODO: Analyze redirect.base.url and safely merge with docusign.app.url
    @Value("${docusign.app.url}")
    private String baseURL;

    @GetMapping("/config/{name}")
    public @ResponseBody AlertConfig getByName(HttpSession session, @PathVariable String name) {
        List<AuthRole> roles = Arrays.asList(AuthRole.ADMIN, AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        AlertConfig alertConfig = alertConfigRepo.findByName(name);
        return alertConfig;
    }

    @GetMapping("/config/auto-fulfillment")
    public @ResponseBody AlertConfig getAutoFulfillment(HttpSession session) {
        AlertConfig alertConfig = alertConfigRepo.findByName(AlertConfigRepo.EIN_FULFILLMENT_AUTO);
        return alertConfig;
    }

    @GetMapping("/config/extension-redeploy")
    public @ResponseBody AlertConfig getExtensionRedeploy(HttpSession session) {
        AlertConfig alertConfig = alertConfigRepo.findByName(AlertConfigRepo.EIN_EXTENSION_REDEPLOY);
        return alertConfig;
    }

    @GetMapping("/config/ein-maintenance")
    public @ResponseBody AlertConfig getEinMaintenance(HttpSession session) {
        AlertConfig alertConfig = alertConfigRepo.findByName(AlertConfigRepo.EIN_SERVICE_MAINTENANCE);
        return alertConfig;
    }

    @GetMapping("/config/list")
    public @ResponseBody Iterable<AlertConfig> getAll(HttpSession session) {
        List<AuthRole> roles = Arrays.asList(AuthRole.ADMIN, AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);
        Iterable<AlertConfig> configs = alertConfigRepo.findAll();
        return configs;
    }

    @PutMapping("/config/{name}")
    public @ResponseBody AlertConfig updateConfig(HttpSession session, @PathVariable String name,
                                                  @RequestBody AlertConfig data) throws NotFoundException {
        authService.requireAdminRole(session);
        AlertConfig foundConfig = alertConfigRepo.findByName(name);
        if (foundConfig == null) {
            LOG.error("ERROR: Not Found AlertConfig: updateConfig()");
            throw new NotFoundException();
        }

        foundConfig.setEmail(data.getEmail());
        foundConfig.setEnabled(data.isEnabled());
        return alertConfigRepo.save(foundConfig);
    }

    @PutMapping("/config/list")
    public @ResponseBody Iterable<AlertConfig> updateAll(HttpSession session, @RequestBody List<AlertConfig> data) {
        authService.requireAdminRole(session);
        List<AlertConfig> toUpdate = new ArrayList<>();
        for (AlertConfig next : data) {
            String name = next.getName();
            AlertConfig foundConfig = alertConfigRepo.findByName(name);
            if (foundConfig == null) {
                LOG.warn("WARNING: Not Found AlertConfig: updateAll()");
                continue;
            }

            foundConfig.setEmail(next.getEmail());
            foundConfig.setEnabled(next.isEnabled());
            toUpdate.add(foundConfig);
        }

        return alertConfigRepo.saveAll(toUpdate);
    }

    @PutMapping("/notify/refund")
    public ResponseEntity notifyRefund(HttpSession session, @RequestBody RefundAlert alert) {
        List<AuthRole> roles = Arrays.asList(AuthRole.ADMIN, AuthRole.STANDARDUSER);
        authService.requireRoles(session, roles);

        List<String> recipients = new ArrayList<>();
        AlertConfig alertConfig1 = alertConfigRepo.findByName(AlertConfigRepo.REFUND_MANUAL_CONFIG);
        if (alertConfig1 != null && alertConfig1.isEnabled()) {
            recipients.add(alertConfig1.getEmail());
        }
        AlertConfig alertConfig2 = alertConfigRepo.findByName(AlertConfigRepo.REFUND_MANUAL_SECONDARY_CONFIG);
        if (alertConfig2 != null && alertConfig2.isEnabled()) {
            recipients.add(alertConfig2.getEmail());
        }

        if (!recipients.isEmpty()) {
            String orderURL = baseURL + alert.getOrderPath();
            mailjetSender.sendRefundAlert(recipients, orderURL);

            return ResponseEntity.ok()
                    .body(new DefaultResponse("Informed", "Manager alerted", "S001"));
        }

        return ResponseEntity.ok()
                .body(new DefaultResponse("Disabled", "Alert disabled", "F001"));
    }

}
