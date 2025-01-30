package com.repnox.nineseventax.features.email;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController()
@RequestMapping("/api/email")
public class EmailController {


    @Autowired
    private MailjetSender mailjetSender;


    @PostMapping("/contact/send")
    public void sendContactEmail(@RequestBody ContactEmail email) throws IOException {
        mailjetSender.sendContactEmail(email);
    }

}
