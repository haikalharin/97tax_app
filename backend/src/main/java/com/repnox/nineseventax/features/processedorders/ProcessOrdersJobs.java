package com.repnox.nineseventax.features.processedorders;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProcessOrdersJobs {

	
	@Autowired
    private ProcessedOrdersRepo processedOrdersRepo;
	@Scheduled(cron = "0 0 1 * * ?")
	public void DeleteProcessOrderJob() {
		try {			
			 Calendar cal = new GregorianCalendar();
		        cal.setTime(new Date());
			cal.add(Calendar.DATE, -15);
	        Date previousMonth = cal.getTime();
	        processedOrdersRepo.deleteByCreatedDateBefore(previousMonth);
		} catch (Exception e) {
			e.printStackTrace();
		}


}
}
