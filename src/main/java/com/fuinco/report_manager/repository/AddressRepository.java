package com.fuinco.report_manager.repository;

import com.fuinco.report_manager.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface AddressRepository extends JpaSpecificationExecutor<Address>, JpaRepository<Address, Integer> {
}
