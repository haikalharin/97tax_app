package com.repnox.nineseventax.features.mailroom.model;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

public class BatchSpecs {

    public static Specification<Batch> searchByStatus() {
        return new Specification<Batch>() {

			@Override
            public Predicate toPredicate(Root<Batch> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
            	predicates.add(cb.equal(root.get(Batch_.status), "Ready")); 
            	predicates.add(cb.equal(root.get(Batch_.status), "Pending")); 
                return cb.or(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
