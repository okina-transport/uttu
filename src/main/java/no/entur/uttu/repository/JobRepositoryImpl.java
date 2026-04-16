package no.entur.uttu.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import no.entur.uttu.model.job.Job;

public class JobRepositoryImpl implements JobRepositoryCustom<Job> {

    @PersistenceContext
    private EntityManager em;

    public Job findBySubFolderLikeReferentialAndId(String subFolder, Long id) {
        String queryString = "SELECT j FROM Job j WHERE j.subFolder = :subFolder AND j.id = :id";
        Query query = em.createQuery(queryString, Job.class);
        query.setParameter("subFolder", subFolder);
        query.setParameter("id", id);
        return (Job) query.getSingleResult();
    }

    public Job terminatedJob(String subFolder, Long id) {
        return findBySubFolderLikeReferentialAndId(subFolder, id);
    }
}
