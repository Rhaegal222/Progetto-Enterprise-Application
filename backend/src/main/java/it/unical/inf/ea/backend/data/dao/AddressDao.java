package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressDao extends JpaRepository<Address,String>, JpaSpecificationExecutor<Address> {
    List<Address> findAllByUserEquals(User user);
}