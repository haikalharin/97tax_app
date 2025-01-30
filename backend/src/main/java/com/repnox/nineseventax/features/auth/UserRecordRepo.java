package com.repnox.nineseventax.features.auth;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface UserRecordRepo extends CrudRepository<UserRecord, String> {

  Optional<UserRecord> findByEmail(String email);

}
