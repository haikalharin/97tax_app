package com.repnox.nineseventax.features.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.repnox.nineseventax.features.mailroom.model.MailRoomUser;
import com.repnox.nineseventax.features.mailroom.model.MailRoomUserRepo;
import com.repnox.nineseventax.features.mailroom.model.MailRoomUserSpecs;

import java.util.Arrays;
import java.util.Optional;

@Component
public class Authenticator {

    @Value("${app.admin.password}")
    private String adminPassword;

    @Autowired
    private UserRecordRepo userRecordRepo;
    
    @Autowired
    private MailRoomUserRepo mailRoomUserRepo;

    public AuthUser authenticate(String username, String password) {
        UserRecord userRecord;
        Optional<UserRecord> userRecordOptional = userRecordRepo.findById(username);
        Optional<UserRecord> userRecordOptionalByEmail = userRecordRepo.findByEmail(username);

        if (userRecordOptional.isPresent()) {
            userRecord = userRecordOptional.get();
        } else if (userRecordOptionalByEmail.isPresent()) {
            userRecord = userRecordOptionalByEmail.get();
        } else {
            Optional<MailRoomUser> optionalMailUser = mailRoomUserRepo.findOne(MailRoomUserSpecs.searchByEmail(username));
        	if (optionalMailUser.isPresent()) {
        		if (optionalMailUser.get().getPassword().equals(password)) {
        			AuthUser authUser = new AuthUser();
                    authUser.setUsername(optionalMailUser.get().getUserName());
                    authUser.setRoles(Arrays.asList(AuthRole.MAILROOMUSER));
                    return authUser;
        		}
            }
            return null;
        }

        if (userRecord.getPassword().equals(password)) {
            AuthUser authUser = new AuthUser();
            authUser.setUsername(userRecord.getUsername());
            if(userRecord.getUserType().equals("admin")) {
                authUser.setRoles(Arrays.asList(AuthRole.ADMIN));                	
            }
            else {
                authUser.setRoles(Arrays.asList(AuthRole.STANDARDUSER));
            }
            return authUser;
        }
        return null;
    }
}
