package no.entur.uttu.repository;

import no.entur.uttu.model.BookingArrangement;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingArrangementRepository extends IdentifiedRepository<BookingArrangement> {

    List<BookingArrangement> findAllByDatasetId(String datasetId);
}
