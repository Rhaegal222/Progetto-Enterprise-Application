package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.Address;
import it.unical.inf.ea.backend.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AddressDao extends JpaRepository<Address, String>, JpaSpecificationExecutor<Address>, PagingAndSortingRepository<Address, String> {
    List<Address> findByUserEquals(User user);
}
