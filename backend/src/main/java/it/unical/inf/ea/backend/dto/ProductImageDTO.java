package it.unical.inf.ea.backend.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductImageDTO {
    private String id;
    private String description;

    @NotNull
    private String urlPhoto;

    /*
    @JsonSetter
    public void setUrlPhoto(String url) {
        if(url != null && !url.startsWith("http"))
            this.urlPhoto = Constants.BASE_PATH + url;
        else
            this.urlPhoto = url;
    }
     */
}
