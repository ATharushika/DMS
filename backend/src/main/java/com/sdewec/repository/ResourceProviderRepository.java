package com.sdewec.repository;

import com.sdewec.entity.ResourceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceProviderRepository extends JpaRepository<ResourceProvider, Long> {
    Optional<ResourceProvider> findByUserId(Long userId);
}
