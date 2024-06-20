package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.PaymentMethod;
import it.unical.inf.ea.backend.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentMethodDao extends JpaRepository<PaymentMethod, String>, JpaSpecificationExecutor<PaymentMethod>, PagingAndSortingRepository<PaymentMethod, String> {
    List<PaymentMethod> findByOwnerUserEquals(User user);
}
