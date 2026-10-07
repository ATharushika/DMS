package com.sdewec.repository;

import com.sdewec.entity.ResourceAllocation;
import com.sdewec.entity.Shelter;
import com.sdewec.entity.ResourceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceAllocationRepository extends JpaRepository<ResourceAllocation, Long> {
    List<ResourceAllocation> findByShelter(Shelter shelter);
    List<ResourceAllocation> findByProvider(ResourceProvider provider);
    List<ResourceAllocation> findByStatus(ResourceAllocation.AllocationStatus status);
    List<ResourceAllocation> findByProviderAndStatus(ResourceProvider provider, ResourceAllocation.AllocationStatus status);
}
