package eu.europa.ec.fisheries.uvms.sales.service.dto.cache;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ReferenceTerritory {

    private String code;
    private String englishName;

    public ReferenceTerritory() {

    }

    public ReferenceTerritory(String code, String englishName) {
        this.code = code;
        this.englishName = englishName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }
}
