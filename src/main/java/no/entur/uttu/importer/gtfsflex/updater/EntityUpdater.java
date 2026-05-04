package no.entur.uttu.importer.gtfsflex.updater;

import javax.validation.constraints.NotNull;

/**
 * Update db entity with new entity entities (do NOT persist changes to database).
 *
 * @param <T> entity class to update
 */
public interface EntityUpdater<T> {

    default boolean update(@NotNull T newEntity, @NotNull T dbEntity) {
        return false;
    }

}
