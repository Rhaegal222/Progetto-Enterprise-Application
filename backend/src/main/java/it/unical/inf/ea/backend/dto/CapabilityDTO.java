package it.unical.inf.ea.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CapabilityDTO {

    String capabilityToken;

    String capabilityUrl;
}
