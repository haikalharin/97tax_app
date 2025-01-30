package com.repnox.nineseventax.features.couponcode.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

public class CouponCodeSpecs {

    private static final Logger LOG = LoggerFactory.getLogger(CouponCodeSpecs.class);

    public static Specification<CouponCode> searchByCode(String code) {
        return new Specification<CouponCode>() {

			@Override
            public Predicate toPredicate(Root<CouponCode> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (code != null) {
                	predicates.add(cb.equal(root.get(CouponCode_.code), code));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
