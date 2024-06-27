package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.PaymentMethod;
import it.unical.inf.ea.backend.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PaymentMethodDao extends JpaRepository<PaymentMethod, String>, JpaSpecificationExecutor<PaymentMethod>, PagingAndSortingRepository<PaymentMethod, String> {
    //Page<PaymentMethodDTO> findAllByDefaultUser_Id(String id, Pageable pageable);
    List<PaymentMethod> findByOwnerUserEquals(User user);

}