package com.repnox.nineseventax.features.mailroom.model;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BatchRepo extends PagingAndSortingRepository<Batch, Long>, JpaSpecificationExecutor<Batch> {

    @Query("Select batch.id, batch.createdAt, batch.status, batch.endOrderNumber, batch.startOrderNumber from Batch batch")
    List<Object[]> findAllBatch();



}
