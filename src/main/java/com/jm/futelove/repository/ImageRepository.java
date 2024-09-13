package com.jm.futelove.repository;

import com.jm.futelove.entity.Image;
import com.jm.futelove.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID>, JpaSpecificationExecutor<User> {
}
