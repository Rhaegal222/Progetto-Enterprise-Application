package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.Savory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SavoryDao extends JpaRepository<Savory, String>, JpaSpecificationExecutor<Savory> {
}
