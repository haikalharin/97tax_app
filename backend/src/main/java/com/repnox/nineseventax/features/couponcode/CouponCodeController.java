package com.repnox.nineseventax.features.couponcode;

import com.repnox.nineseventax.features.auth.AuthService;
import com.repnox.nineseventax.features.couponcode.model.CouponCode;
import com.repnox.nineseventax.features.couponcode.model.CouponCodeRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.util.*;

@RestController()
@RequestMapping("/api/couponcodes")
public class CouponCodeController {

    @Autowired
    private AuthService authService;

    @Autowired
    private CouponCodeRepo couponCodeRepo;

    @GetMapping("/")
    public @ResponseBody ArrayList<CouponCode> getCouponCodes(HttpSession session) {
    	authService.requireAdminRole(session);
    	ArrayList<CouponCode> couponCodes = (ArrayList<CouponCode>)couponCodeRepo.findAll();
    	return couponCodes;
    }
}
