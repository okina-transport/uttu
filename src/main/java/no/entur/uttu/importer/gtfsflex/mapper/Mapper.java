package no.entur.uttu.importer.gtfsflex.mapper;

import no.entur.uttu.importer.gtfsflex.Referential;

import javax.validation.constraints.NotNull;

public interface Mapper<U> {

    void map(@NotNull U gtfsEntity, @NotNull Referential gtfsImportReferential);

}
