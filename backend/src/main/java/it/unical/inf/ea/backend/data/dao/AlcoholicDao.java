package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.Alcoholic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AlcoholicDao extends JpaRepository<Alcoholic, String>, JpaSpecificationExecutor<Alcoholic> {
}
