package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.Report;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.dto.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportDao extends JpaRepository<Report,String>, JpaSpecificationExecutor<Report> {
    Page<Report> findAll(Pageable pageable);

    Page<Report> findByStatusOrderByDateAsc(ReportStatus status, Pageable pageable);
    Page<Report> findByStatusAndAdminFollowedReportOrderByLastUpdateDesc(ReportStatus status, User user, Pageable pageable);
}
