package no.entur.uttu.job;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.UriInfo;
import no.entur.uttu.model.job.*;
import no.entur.uttu.repository.JobRepository;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Transactional
public class JobService {

    private final JobRepository jobRepository;

    JobService(JobRepository jobRepository){
        this.jobRepository = jobRepository;
    }

    public Job getJobById(long id) {
        return jobRepository.findById(id).orElse(null);
    }

    public Job scheduledJob(String subFolder, Long id) throws org.hibernate.service.spi.ServiceException {
        return getJobService(subFolder, id);
    }

    public Job getJobService(String subFolder, Long id) throws org.hibernate.service.spi.ServiceException {

        Job job = jobRepository.findBySubFolderLikeReferentialAndId(subFolder, id);
        if (job != null) {
            return job;
        }
        throw new ServiceException("subFolder = " + subFolder + " ,id = " + id);
    }

    public Job createImportJob(String fileName, String folder, String username, String provider) {
        Job job = new Job();
        job.setFileName(fileName);
        job.setType(JobType.GTFS);
        job.setAction(JobAction.IMPORT);
        job.setStatus(JobStatus.PROCESSING);
        job.setStarted(Instant.now());
        job.setSubFolder(folder);
        job.setUserName(username);
        job.setProvider(provider);
        return jobRepository.save(job);
    }

    public Job createExportJob(String fileName, String folder, String username, String provider) {
        Job job = new Job();
        job.setFileName(fileName);
        job.setType(JobType.GTFS);
        job.setAction(JobAction.EXPORT);
        job.setStatus(JobStatus.PROCESSING);
        job.setStarted(Instant.now());
        job.setSubFolder(folder);
        job.setUserName(username);
        job.setProvider(provider);
        return jobRepository.save(job);
    }

    public void updateJob(Job job, JobStatus status, String fileName, String errorMessage) {
        job.setStatus(status);
        job.setFinished(Instant.now());
        job.setFileName(fileName);
        job.setMessage(errorMessage);
        jobRepository.save(job);
    }

    public JobInfo getTerminatedJob(Long jobId, String referential, UriInfo uriInfo) {
        Job job = jobRepository.terminatedJob(referential, jobId);
        return new JobInfo(job, uriInfo);
    }
}

