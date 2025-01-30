package com.repnox.nineseventax.features.processordersschedule;

import org.springframework.data.repository.CrudRepository;

public interface ProcessOrdersScheduleRepo extends CrudRepository<ProcessOrdersSchedule, Long> {
    ProcessOrdersSchedule findById(Integer id);  
}
