package com.repnox.nineseventax.features.partnercode;

import com.repnox.nineseventax.features.auth.AuthService;
import com.repnox.nineseventax.features.partnercode.model.PartnerCode;
import com.repnox.nineseventax.features.partnercode.model.PartnerCodeRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.util.*;

@RestController()
@RequestMapping("/api/partnercodes")
public class PartnerCodeController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PartnerCodeRepo partnerCodeRepo;

    @GetMapping("/")
    public @ResponseBody ArrayList<PartnerCode> getPartnerCodes(HttpSession session) {
    	authService.requireAdminRole(session);
    	ArrayList<PartnerCode> partnerCodes = (ArrayList<PartnerCode>)partnerCodeRepo.findAll();
    	return partnerCodes;
    }
}
