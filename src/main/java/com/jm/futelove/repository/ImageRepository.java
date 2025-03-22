package com.jm.futelove.repository;

import com.jm.futelove.entity.Image;
import com.jm.futelove.entity.Users;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends CrudRepository<Image, UUID>, JpaSpecificationExecutor<Users> {

    List<Image> findByUsersId(UUID userId);
}
