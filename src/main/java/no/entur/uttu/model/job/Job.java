package no.entur.uttu.model.job;

import com.google.common.base.MoreObjects;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job")
@SequenceGenerator(
        name = "job_seq_gen",
        sequenceName = "job_seq",
        allocationSize = 10
)
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_seq_gen")
    private Long id;

    private String jobUrl;

    private String fileName;

    private String message;

    private Instant started;

    private Instant finished;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Enumerated(EnumType.STRING)
    private JobType type;

    @Enumerated(EnumType.STRING)
    private JobAction action;

    private String userName;

    private String provider;

    private String subFolder;

    @Transient
    private List<Link> links = new ArrayList<>();

    public Job() {
    }

    public Job(JobStatus jobStatus) {
        this.status = jobStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getStarted() {
        return started;
    }

    public void setStarted(Instant started) {
        this.started = started;
    }

    public Instant getFinished() {
        return finished;
    }

    public void setFinished(Instant finished) {
        this.finished = finished;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public JobAction getAction() {
        return action;
    }

    public void setAction(JobAction action) {
        this.action = action;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getSubFolder() {
        return subFolder;
    }

    public void setSubFolder(String subfolder) {
        this.subFolder = subfolder;
    }

    public List<Link> getLinks() {
        return links;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("status", status)
                .add("type", type)
                .add("jobUrl", jobUrl)
                .add("fileName", fileName)
                .add("started", started)
                .add("finished", finished)
                .add("message", message)
                .add("userName", userName)
                .toString();
    }
}
