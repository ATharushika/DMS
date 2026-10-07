package com.sdewec.repository;

import com.sdewec.entity.ResourceDelivery;
import com.sdewec.entity.ResourceAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceDeliveryRepository extends JpaRepository<ResourceDelivery, Long> {
    List<ResourceDelivery> findByAllocation(ResourceAllocation allocation);
}
