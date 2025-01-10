package no.entur.uttu.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import no.entur.uttu.repository.CompanyRegistry;
import org.rutebanken.netex.model.GeneralOrganisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("companiesFetcher")
public class CompaniesFetcher implements DataFetcher<List<GeneralOrganisation>> {

    @Autowired
    CompanyRegistry companyRegistry;

    @Override
    public List<GeneralOrganisation> get(DataFetchingEnvironment environment) throws Exception {

        return companyRegistry.getCompanies();
    }
}
