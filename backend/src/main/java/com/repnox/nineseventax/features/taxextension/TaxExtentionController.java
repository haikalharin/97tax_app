package com.repnox.nineseventax.features.taxextension;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;



@RestController()
@RequestMapping("/api/taxextension")

public class TaxExtentionController {

	@PostMapping("/validate")

	public @ResponseBody boolean taxExtensionLogin(HttpSession session,@RequestBody TaxExtentionDetail taxExtensionDetail) {
		
		return taxExtensionDetail.checkPasswordIsMatch();
		
	}
}
