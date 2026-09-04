package no.entur.uttu.repository;

import no.entur.uttu.model.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface JobRepository extends PagingAndSortingRepository<Job, Long>, JobRepositoryCustom<Job>, JpaRepository<Job, Long>,
        JpaSpecificationExecutor<Job> {

    Optional<Job> findByCorrelationId(String correlationId);
}
