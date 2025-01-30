package com.repnox.nineseventax.features.partnercode.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

public class PartnerCodeSpecs {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerCodeSpecs.class);

    public static Specification<PartnerCode> searchByCode(String code) {
        return new Specification<PartnerCode>() {

			@Override
            public Predicate toPredicate(Root<PartnerCode> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (code != null) {
                	predicates.add(cb.equal(root.get(PartnerCode_.code), code));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
