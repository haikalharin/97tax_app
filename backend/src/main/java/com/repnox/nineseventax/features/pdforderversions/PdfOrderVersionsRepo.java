package com.repnox.nineseventax.features.pdforderversions;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PdfOrderVersionsRepo extends CrudRepository<PdfOrderVersions, Long> {

    @Query("from PdfOrderVersions where orderId=?1")
    ArrayList<PdfOrderVersions> findByOrderid(String orderId);
    
    @Query("from PdfOrderVersions where versionId=?1")
    ArrayList<PdfOrderVersions> findByVersionId(String versionId);

    @Query("from PdfOrderVersions where orderId=?1 and createdDate=?2")
    Optional<PdfOrderVersions> findByDateAndOrderId(String orderId, Date date);

    @Query(nativeQuery = true, value = "SELECT id, null as pdf_file, order_id, created_date, version_id FROM pdf_order_versions where order_id=?1 ORDER BY created_date ASC")
    ArrayList<PdfOrderVersions> findMetaInfoByOrderid(String orderId);
}
