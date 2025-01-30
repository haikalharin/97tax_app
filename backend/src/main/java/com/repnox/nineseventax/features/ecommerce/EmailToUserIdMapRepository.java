package com.repnox.nineseventax.features.ecommerce;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailToUserIdMapRepository extends JpaRepository<EmailToUserIdMap, String> {
    
    @Query("SELECT COALESCE(MAX(e.userId), 0) FROM EmailToUserIdMap e")
    Long findMaxUserId();
}