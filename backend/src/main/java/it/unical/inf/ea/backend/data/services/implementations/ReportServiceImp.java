package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.ReportDao;
import it.unical.inf.ea.backend.data.entities.Report;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.interfaces.ProductService;
import it.unical.inf.ea.backend.data.services.interfaces.ReportService;
import it.unical.inf.ea.backend.data.services.interfaces.UserService;
import it.unical.inf.ea.backend.dto.ProductDTO;
import it.unical.inf.ea.backend.dto.ReportDTO;
import it.unical.inf.ea.backend.dto.enums.ReportStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import it.unical.inf.ea.backend.dto.enums.*;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportServiceImp implements ReportService {
    private final ReportDao reportDao;
    //private final JwtContextUtils jwtContextUtils;

    private final ProductService productService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public ReportDTO createReport(ReportDTO reportDTO) {
        /*
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (!loggedUser.getId().equals(reportDTO.getReporterUser().getId())
                || userService.getUserById(reportDTO.getReportedUser().getId()) == null
                || reportDTO.getReportedUser().getId().equals(reportDTO.getReporterUser().getId()) ) { //era reportDTO.find
            throw new RuntimeException("User not allowed to report");
        }

        if (reportDTO.getReportedProduct() != null) {
            ProductDTO productDTO = productService.getProductById(Long.valueOf(reportDTO.getReportedProduct().getId()));
            if (productDTO == null) {
                throw new RuntimeException("Product not found");
            }
        }
        reportDTO.setDescription(reportDTO.getDescription().trim());
        reportDTO.setStatus(ReportStatus.PENDING);
        reportDTO.setDate(LocalDateTime.now());
        reportDTO.setLastUpdate(LocalDateTime.now());
        Report report = mapToEntity(reportDTO);
        return mapToDto(reportDao.save(report));
         */
        return null;
    }

    @Override
    public ReportDTO closeReport(String reportId) {
        /*
        Report report = reportDao.findById(reportId).orElseThrow(() -> new RuntimeException("Report not found"));
        if(reportDao.findById(reportId).isEmpty()) {
            throw new RuntimeException("Report not found");
        }
        report.setStatus(ReportStatus.CLOSED);
        report = reportDao.save(report);
        return mapToDto(report);
         */
        return null;
    }

    @Override
    public Page<ReportDTO> getReportsByStatus(ReportStatus status, int page, int size) {
        return reportDao.findByStatusOrderByDateAsc(status, PageRequest.of(page, size)).map(this::mapToDto);
    }

    @Override
    public Page<ReportDTO> getReportsMeManaging(int page, int size) throws IllegalAccessException {
        /*
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getRole().equals(UserRole.USER))
            throw new IllegalAccessException("You can't access this page");
        return reportDao.findByStatusAndAdminFollowedReportOrderByLastUpdateDesc(ReportStatus.PENDING,loggedUser,PageRequest.of(page,size)).map(this::mapToDto);
         */
        return null;
    }

    @Override
    public ReportDTO updateReport(String id) throws IllegalAccessException {
        /*
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getRole().equals(UserRole.USER))
            throw new IllegalAccessException("You can't modify report");

        Report report = reportDao.findById(id).orElseThrow(EntityNotFoundException::new);
        report.setLastUpdate(LocalDateTime.now());
        report.setAdminFollowedReport(loggedUser);
        return modelMapper.map(reportDao.save(report),ReportDTO.class) ;
         */
        return null;
    }


    public ReportDTO mapToDto(Report report) { return modelMapper.map(report, ReportDTO.class); }

    public Report mapToEntity(ReportDTO reportDTO) { return modelMapper.map(reportDTO, Report.class); }
}
