package no.entur.uttu.graphql.mappers;

import no.entur.uttu.graphql.ArgumentWrapper;
import no.entur.uttu.model.Line;
import no.entur.uttu.model.Network;
import no.entur.uttu.repository.CompanyRegistry;
import no.entur.uttu.repository.ProviderRepository;
import no.entur.uttu.repository.RemoteNetworkRepository;
import no.entur.uttu.repository.generic.ProviderEntityRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static no.entur.uttu.graphql.GraphQLNames.*;

@Component
public abstract class LineMapper<T extends Line> extends AbstractGroupOfEntitiesMapper<T> {

    private final RemoteNetworkRepository networkRepository;
    private final JourneyPatternMapper journeyPatternMapper;
    private final NoticeMapper noticeMapper;
    private final CompanyRegistry companyRegistry;

    protected LineMapper(ProviderRepository providerRepository, ProviderEntityRepository<T> repository, RemoteNetworkRepository networkRepository, JourneyPatternMapper journeyPatternMapper, NoticeMapper noticeMapper, CompanyRegistry companyRegistry) {
        super(providerRepository, repository);
        this.networkRepository = networkRepository;
        this.journeyPatternMapper = journeyPatternMapper;
        this.noticeMapper = noticeMapper;
        this.companyRegistry = companyRegistry;
    }

    @Override
    protected void populateEntityFromInput(T entity, ArgumentWrapper input) {
        input.apply(FIELD_NAME, entity::setName);
        input.apply(FIELD_PUBLIC_CODE, entity::setPublicCode);
        input.apply(FIELD_TRANSPORT_MODE, entity::setTransportMode);
        input.apply(FIELD_TRANSPORT_SUBMODE, entity::setTransportSubmode);
        setNetwork(entity, input.get(FIELD_NETWORK_REF));
        input.apply(FIELD_OPERATOR_REF, companyRegistry::getVerifiedOperatorRef, entity::setOperatorRef);
        input.applyList(FIELD_JOURNEY_PATTERNS, journeyPatternMapper::map, entity::setJourneyPatterns);
        input.applyList(FIELD_NOTICES, noticeMapper::map, entity::setNotices);
    }

    private void setNetwork(T entity, String networkRef) {
        if (StringUtils.isEmpty(networkRef)) {
            throw new IllegalArgumentException("Empty network reference :" + networkRef);
        }

        List<Network> existingNetworks = networkRepository.syncAndFindAll();
        Optional<Network> foundNetworkOpt = existingNetworks.stream()
                .filter(network -> networkRef.equals(network.getNetexId()))
                .findFirst();

        if (foundNetworkOpt.isEmpty()) {
            throw new IllegalArgumentException("Unknown network reference :" + networkRef);
        }
        entity.setNetwork(foundNetworkOpt.get());


    }
}
