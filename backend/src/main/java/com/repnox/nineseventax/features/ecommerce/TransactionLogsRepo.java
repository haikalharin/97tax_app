package com.repnox.nineseventax.features.ecommerce;

import com.repnox.nineseventax.features.partnercode.model.PCUsageReport;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;


public interface TransactionLogsRepo
        extends PagingAndSortingRepository<TransactionLogs, Long>, JpaSpecificationExecutor<TransactionLogs> {

}
