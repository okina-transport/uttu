package no.entur.uttu.model;


import java.math.BigDecimal;

import java.util.List;



public class StopPlaceView {

    
    private String id;

    
    private String name;

    
    private String netexId;

    
    private BigDecimal latitude;

    
    private BigDecimal longitude;

    
    private String importedId;

    
    private long version;

    
    private String created;

    
    private String fromDate;

    
    private String toDate;

    
    private List<QuayView> quays;

    public StopPlaceView(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public List<QuayView> getQuays() {
        return quays;
    }

    public void setQuays(List<QuayView> quays) {
        this.quays = quays;
    }
}