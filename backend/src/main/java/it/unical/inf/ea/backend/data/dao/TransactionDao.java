package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDao extends JpaRepository<Transaction,String >, JpaSpecificationExecutor<Transaction> {
}
