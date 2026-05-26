package no.entur.uttu.importer.gtfsflex.mapper;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.model.Codespace;
import no.entur.uttu.model.Network;
import no.entur.uttu.model.Provider;
import org.onebusaway.gtfs.model.Agency;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AgencyMapper implements Mapper<Agency> {

    @Override
    public void map(Agency gtfsEntity, Referential gtfsImportReferential) {
        log.info("Mapping agency {}", gtfsEntity.getId());
        Provider provider = gtfsImportReferential.getProvider(gtfsEntity.getId());
        provider.setName(gtfsEntity.getName());
        provider.setCode(gtfsImportReferential.getDataset().toLowerCase());

        Codespace codespace = gtfsImportReferential.getCodespace(gtfsEntity.getId());
        codespace.setXmlns(gtfsImportReferential.getDataset().toLowerCase());
        codespace.setXmlnsUrl("http://" + gtfsImportReferential.getDataset().toLowerCase());
        provider.setCodespace(codespace);

        Network network = gtfsImportReferential.getNetwork(gtfsEntity.getId());
        network.setName(gtfsEntity.getName());
        network.setAuthorityRef(String.format("%s:Authority:%s", gtfsImportReferential.getDataset().toUpperCase(), gtfsEntity.getId()));
        network.setProvider(provider);

        log.debug("gtfsEntity {}", gtfsEntity);
        log.debug("provider {}", provider);
        log.debug("codespace {}", codespace);
        log.debug("network {}", network);
    }

}
