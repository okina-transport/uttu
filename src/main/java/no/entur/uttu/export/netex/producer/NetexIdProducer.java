package no.entur.uttu.export.netex.producer;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import no.entur.uttu.export.netex.NetexExportContext;
import no.entur.uttu.model.ProviderEntity;
import no.entur.uttu.model.Ref;
import org.rutebanken.netex.model.EntityInVersionStructure;
import org.rutebanken.netex.model.EntityStructure;
import org.rutebanken.netex.model.VersionOfObjectRefStructure;

import java.util.Objects;

public class NetexIdProducer {

    private static String SEPARATOR = ":";

    public static Ref replaceEntityName(Ref ref, String newEntityName) {
        String id = getId(NetexIdProducer.getObjectIdPrefix(ref.id), newEntityName, NetexIdProducer.getObjectIdSuffix(ref.id));
        return new Ref(id, ref.version);
    }

    public static <N extends EntityInVersionStructure> String getId(N netex, Ref ref) {
        return getId(NetexIdProducer.getObjectIdPrefix(ref.id), netex.getClass().getSimpleName(), NetexIdProducer.getObjectIdSuffix(ref.id));
    }

    public static <N extends VersionOfObjectRefStructure> String getReference(N netex, Ref ref) {
        return getId(NetexIdProducer.getObjectIdPrefix(ref.id), getReferedEntityName(netex), NetexIdProducer.getObjectIdSuffix(ref.id));
    }

    public static <N extends EntityInVersionStructure, L extends ProviderEntity> N copyIdAndVersion(N netex, L local) {
        netex.setId(local.getNetexId());
        netex.setVersion(Objects.toString(local.getNetexVersion()));
        return netex;
    }

    public static <E extends EntityStructure> String generateId(Class<E> netexEntityClass, NetexExportContext context) {
        String entityName = netexEntityClass.getSimpleName();
        return getId(getIdPrefix(context), entityName, generateIdSuffix(entityName, context));
    }

    public static <E extends EntityStructure> String getId(Class<E> netexEntityClass, String suffix, NetexExportContext context) {
        return Joiner.on(SEPARATOR).join(getIdPrefix(context), netexEntityClass.getSimpleName(), suffix);
    }

    public static String getId(String prefix, String entityName, String suffix) {
        return Joiner.on(SEPARATOR).join(prefix, entityName, suffix);
    }

    private static String generateIdSuffix(String entityName, NetexExportContext context) {
        return Objects.toString(context.getAndIncrementIdSequence(entityName));
    }

    private static String getIdPrefix(NetexExportContext context) {
        return context.provider.getCodespace().getXmlns();
    }

    public static String getObjectIdPrefix(String objectId) {
        return objectId.split(SEPARATOR)[0];
    }

    public static String getObjectIdSuffix(String objectId) {
        return Iterables.getLast(Splitter.on(SEPARATOR)
                                         .trimResults()
                                         .split(objectId));
    }

    /**
     * Returns the name of the Netex entity or the referred entity if Netex class is a refererence type.
     */
    public static <E> String getEntityName(E entity) {
        String localPart = entity.getClass().getSimpleName();

        if (entity instanceof VersionOfObjectRefStructure) {
            // Assuming all VersionOfObjectRefStructure subclasses is named as correct element + suffix ("RefStructure""))
            if (localPart.endsWith("Structure")) {
                localPart = localPart.substring(0, localPart.lastIndexOf("Structure"));
            }
        }
        return localPart;
    }

    /**
     * Returns the name of the Netex entity or the referred entity if Netex class is a refererence type.
     */
    public static <E> String getReferedEntityName(E entity) {
        String localPart = entity.getClass().getSimpleName();

        if (entity instanceof VersionOfObjectRefStructure) {
            // Assuming all VersionOfObjectRefStructure subclasses is named as correct element + suffix ("RefStructure"))
            if (localPart.endsWith("RefStructure")) {
                localPart = localPart.substring(0, localPart.lastIndexOf("RefStructure"));
            }
        }
        return localPart;
    }

}

