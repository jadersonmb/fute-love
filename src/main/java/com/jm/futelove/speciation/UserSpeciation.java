package com.jm.futelove.speciation;

import com.jm.futelove.dto.UserDTO;
import com.jm.futelove.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserSpeciation {

    public static Specification<User> search(UserDTO filter) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Objects.nonNull(filter.getName()) && !filter.getName().isEmpty()) {
                predicates.add(builder.like(builder.lower(root.<String>get("name")),
                        "%".concat(filter.getName().toLowerCase()).concat("%")));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
