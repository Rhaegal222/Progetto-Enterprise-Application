package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SweetDao extends JpaRepository<Sweet, String>, JpaSpecificationExecutor<Sweet> {
}
