package com.repnox.nineseventax.features.ecommerce;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailToUserIdMapService {

    @Autowired
    private EmailToUserIdMapRepository repository;

    @Transactional
    public EmailToUserIdMap createRecordWithEmail(String email) {
        email = cleanEmail(email);
        Long userId = repository.findMaxUserId() + 1;

        while (true) {
            try {
                EmailToUserIdMap newRecord = new EmailToUserIdMap(email, userId);
                return repository.save(newRecord);
            } catch (DataIntegrityViolationException e) {
                // If a conflict occurs, increment userId and retry
                userId++;
            }
        }
    }

    @Transactional
    public EmailToUserIdMap getOrCreateRecordByEmail(String email) {
        final String cleanedEmail = cleanEmail(email);
        Optional<EmailToUserIdMap> existingRecord = repository.findById(cleanedEmail);
        return existingRecord.orElseGet(() -> createRecordWithEmail(cleanedEmail));
    }

    private String cleanEmail(String email) {
        email = email.toLowerCase();
        if (email.endsWith("@gmail.com") || email.endsWith("@googlemail.com")) {
            int atIndex = email.indexOf('@');
            String localPart = email.substring(0, atIndex).replace(".", "");
            String domainPart = email.substring(atIndex);
            email = localPart + domainPart;
        }
        return email;
    }
}