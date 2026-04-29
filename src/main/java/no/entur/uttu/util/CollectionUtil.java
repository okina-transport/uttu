package no.entur.uttu.util;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;

public class CollectionUtil {

    private CollectionUtil() {
        /* This utility class should not be instantiated */
    }

    public static boolean isEqualCollectionNullSafe(final Collection<?> a, final Collection<?> b) {
        return a == b || CollectionUtils.isEqualCollection(a, b);
    }

}
