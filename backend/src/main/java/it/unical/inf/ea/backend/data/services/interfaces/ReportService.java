package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.ReportDTO;
import it.unical.inf.ea.backend.dto.enums.ReportStatus;
import org.springframework.data.domain.Page;

public interface ReportService {
    ReportDTO createReport(ReportDTO reportDTO);

    ReportDTO closeReport(String reportId);

    Page<ReportDTO> getReportsByStatus(ReportStatus status, int page, int size);

    Page<ReportDTO> getReportsMeManaging(int page, int size) throws IllegalAccessException;

    ReportDTO updateReport(String id) throws IllegalAccessException;
}
