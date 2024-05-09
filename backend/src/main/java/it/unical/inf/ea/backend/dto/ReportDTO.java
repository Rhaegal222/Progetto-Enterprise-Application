package it.unical.inf.ea.backend.dto;

import it.unical.inf.ea.backend.dto.basics.ProductBasicDTO;
import it.unical.inf.ea.backend.dto.basics.UserBasicDTO;
import it.unical.inf.ea.backend.dto.enums.ReportStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
public class ReportDTO {

    private String id;

    private UserBasicDTO reporterUser;

    @Length(max = 200)
    private String description;

    private UserBasicDTO reportedUser;

    private ProductBasicDTO reportedProduct;

    private LocalDateTime date;

    private LocalDateTime lastUpdate;

    private ReportStatus status;

    private UserBasicDTO adminFollowedReport;
}
