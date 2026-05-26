package no.entur.uttu.model.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HasDataDTO {
    private String provider;
    private boolean hasData;

}
