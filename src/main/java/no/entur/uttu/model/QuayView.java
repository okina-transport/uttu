package no.entur.uttu.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "quay")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuayView {

    @XmlElement
    private Long id;

    @XmlElement
    private String name;

    @XmlElement
    private String netexId;

    @XmlElement
    private BigDecimal latitude;

    @XmlElement
    private BigDecimal longitude;

    @XmlElement
    private String importedId;


    public QuayView(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetexId() {
        return netexId;
    }

    public void setNetexId(String netexId) {
        this.netexId = netexId;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getImportedId() {
        return importedId;
    }

    public void setImportedId(String importedId) {
        this.importedId = importedId;
    }
}