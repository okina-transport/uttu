package no.entur.uttu.repository;

import org.rutebanken.netex.model.GeneralOrganisation;

import java.util.List;
import java.util.Optional;

public interface CompanyRegistry {
    List<GeneralOrganisation> getCompanies();
    Optional<GeneralOrganisation> getOperator(String operatorRef);
    Optional<GeneralOrganisation> getAuthority(String authorityRef);
    String getVerifiedAuthorityRef(String authorityRef);
    String getVerifiedOperatorRef(String operatorRef);

}
