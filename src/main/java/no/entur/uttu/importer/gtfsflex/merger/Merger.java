package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;

/**
 * Saves an entity (persist changes to database).
 *
 * @param <T> entity class to merge
 */
public interface Merger<T> {
    T merge(T entity, Referential dbReferential, boolean persist);
}
