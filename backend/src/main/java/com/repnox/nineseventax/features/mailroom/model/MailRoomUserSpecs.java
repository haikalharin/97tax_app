package com.repnox.nineseventax.features.mailroom.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

public class MailRoomUserSpecs {

    private static final Logger LOG = LoggerFactory.getLogger(MailRoomUserSpecs.class);

    public static Specification<MailRoomUser> searchByEmail(String email) {
        return new Specification<MailRoomUser>() {

			@Override
            public Predicate toPredicate(Root<MailRoomUser> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (email != null) {
                	predicates.add(cb.equal(root.get(MailRoomUser_.email), email)); 
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
