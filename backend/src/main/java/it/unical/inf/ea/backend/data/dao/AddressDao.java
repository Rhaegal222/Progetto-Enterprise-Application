package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.Address;
import it.unical.inf.ea.backend.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface AddressDao extends JpaRepository<Address, UUID>, JpaSpecificationExecutor<Address> {

    List<Address> findAllByUserId(UUID id);
}
