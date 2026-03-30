package no.entur.uttu.repository;

public interface JobRepositoryCustom<Job> {
    Job findBySubFolderLikeReferentialAndId(String subFolder, Long id);

    Job terminatedJob(String referential, Long id);
}
