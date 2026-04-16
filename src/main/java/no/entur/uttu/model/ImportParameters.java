package no.entur.uttu.model;

public class ImportParameters {
    private String fileName;
    private Long providerId;
    private String provider;
    private String user;

    // getters/setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
}
