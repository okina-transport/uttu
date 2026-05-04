package no.entur.uttu.repository;

import no.entur.uttu.model.BookingArrangement;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingArrangementRepository extends IdentifiedRepository<BookingArrangement> {
}
