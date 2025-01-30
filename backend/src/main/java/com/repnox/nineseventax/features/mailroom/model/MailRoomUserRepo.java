package com.repnox.nineseventax.features.mailroom.model;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MailRoomUserRepo extends PagingAndSortingRepository<MailRoomUser, Long>, JpaSpecificationExecutor<MailRoomUser> {

}
