package com.repnox.nineseventax.features.auth;

import com.repnox.nineseventax.exceptions.NotFoundException;
import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.email.PasswordResetEmail;
import com.repnox.nineseventax.features.mailroom.model.MailRoomUser;
import com.repnox.nineseventax.features.mailroom.model.MailRoomUserRepo;
import com.repnox.nineseventax.features.mailroom.model.MailRoomUserSpecs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@RestController()
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordResetRequestRepo passwordResetRequestRepo;

    @Autowired
    private UserRecordRepo userRecordRepo;

    @Autowired
    private MailRoomUserRepo mailRoomUserRepo;

    @Autowired
    private MailjetSender mailjetSender;

    @PostMapping("/login")
    public Object login(HttpSession session, @RequestBody AuthUser authUser) {
        AuthSession authSession = authService.login(session, authUser.getUsername(), authUser.getPassword());
        AuthUser foundUser = authSession.getAuthUser();
        List<AuthRole> userRoles = foundUser.getRoles();
        String username = foundUser.getUsername();

    	if (userRoles.contains(AuthRole.ADMIN) || userRoles.contains(AuthRole.STANDARDUSER)) {
        	UserRecord ur = userRecordRepo.findById(username).get();
    		return ur;
        }
        
        Optional<MailRoomUser> mru = mailRoomUserRepo.findOne(MailRoomUserSpecs.searchByEmail(authUser.getUsername()));
    	return mru.get();
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        authService.logout(session);
    }

    @GetMapping("/reset-request/{uuid}")
    public PasswordResetRequest getResetRequest(@PathVariable String uuid) {
        Optional<PasswordResetRequest> optionalPasswordResetRequest = passwordResetRequestRepo.findById(uuid);
        if (optionalPasswordResetRequest.isPresent()) {
            return optionalPasswordResetRequest.get();
        } else {
            throw new NotFoundException();
        }
    }

    @PostMapping("/reset-email")
    public void reset(HttpServletRequest request, @RequestBody AuthUser authUser) throws Exception {
        Optional<UserRecord> optionalUserRecord = userRecordRepo.findById(authUser.getUsername());
        if (optionalUserRecord.isPresent()) {
            PasswordResetRequest resetRequest = new PasswordResetRequest();
            resetRequest.setUuid(UUID.randomUUID().toString());
            resetRequest.setUsername(authUser.getUsername());
            passwordResetRequestRepo.save(resetRequest);
            PasswordResetEmail passwordResetEmail = new PasswordResetEmail();

            System.out.println(" ==================== Domain Name ========= " + authUser.getDomain());
            passwordResetEmail.setResetUrl("https://"+authUser.getDomain()+"/password-reset/"+resetRequest.getUuid());
            passwordResetEmail.setToEmail(optionalUserRecord.get().getEmail());
            mailjetSender.sendPasswordResetEmail(passwordResetEmail);
        }
    }

    @PostMapping("/reset")
    public void reset(HttpServletRequest request, @RequestBody PasswordResetRequest passwordResetRequest) throws Exception {
        Optional<PasswordResetRequest> optionalPasswordResetRequest = passwordResetRequestRepo.findById(passwordResetRequest.getUuid());

        if (optionalPasswordResetRequest.isPresent()) {
            PasswordResetRequest existingResetRequest = optionalPasswordResetRequest.get();
            passwordResetRequestRepo.delete(existingResetRequest);
            Optional<UserRecord> optionalUserRecord = userRecordRepo.findById(passwordResetRequest.getUsername());
            if (optionalUserRecord.isPresent()) {
                UserRecord userRecord = optionalUserRecord.get();
                userRecord.setPassword(passwordResetRequest.getPassword());
                userRecordRepo.save(userRecord);
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new NotFoundException();
        }

    }

}
