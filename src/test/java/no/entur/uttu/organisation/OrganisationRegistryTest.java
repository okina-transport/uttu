package no.entur.uttu.organisation;

import org.junit.Test;

public class OrganisationRegistryTest {

// TODO remove
    @Test
    public void t() {
        OrganisationRegistry organisationRegistry = new OrganisationRegistry("https://tjenester.entur.org/organisations/v1/organisations/");

        Organisation org = organisationRegistry.getOrganisation("20");
        toString();
    }
}
