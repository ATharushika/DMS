package com.sdewec.repository;

import com.sdewec.entity.Resource;
import com.sdewec.entity.ResourceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByProvider(ResourceProvider provider);
    List<Resource> findByResourceType(String resourceType);
}
