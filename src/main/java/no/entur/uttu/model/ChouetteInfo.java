/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */

package no.entur.uttu.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChouetteInfo {

    public Long id;
    public String xmlns;
    public String xmlnsurl;
    public String referential;
    public String organisation;
    public String user;
    public String regtoppVersion;
    public String regtoppCoordinateProjection;
    public String regtoppCalendarStrategy;
    public String dataFormat;
    public boolean enableValidation = false;
    public boolean allowCreateMissingStopPlace = false;
    public boolean enableStopPlaceIdMapping = false;
    public boolean enableCleanImport = false;
    public boolean enableAutoImport;
    public boolean generateDatedServiceJourneyIds;
    public Set<String> generateMissingServiceLinksForModes;
    public boolean idfm;
    public String codeIdfm;

    public Long migrateDataToProvider; // Which dataspace to transfer data to when provider dataspace is valid

    public Long getMigrateDataToProvider() {
        return migrateDataToProvider;
    }

    @Override
    public String toString() {
        return "ChouetteInfo{" +
                "id=" + id +
                ", xmlns='" + xmlns + '\'' +
                ", xmlnsurl='" + xmlnsurl + '\'' +
                ", referential='" + referential + '\'' +
                ", organisation='" + organisation + '\'' +
                ", user='" + user + '\'' +
                ", regtoppVersion='" + regtoppVersion + '\'' +
                ", regtoppCoordinateProjection='" + regtoppCoordinateProjection + '\'' +
                ", regtoppCalendarStrategy='" + regtoppCalendarStrategy + '\'' +
                ", dataFormat='" + dataFormat + '\'' +
                ", enableValidation='" + enableValidation + '\'' +
                ", allowCreateMissingStopPlace='" + allowCreateMissingStopPlace + '\'' +
                ", enableStopPlaceIdMapping='" + enableStopPlaceIdMapping + '\'' +
                ", enableCleanImport='" + enableCleanImport + '\'' +
                ", generateMissingServiceLinksForModes='" + generateMissingServiceLinksForModes + '\'' +
                ", migrateDataToProvider='" + migrateDataToProvider + '\'' +
                ", generateDatedServiceJourneyIds='" + generateDatedServiceJourneyIds + '\'' +
                ", idfm=" + idfm +
                ", codeIdfm=" + codeIdfm +
                '}';
    }

    public boolean usesRegtopp(){
        return "regtopp".equals(dataFormat);
    }

    public Long getId() {
        return id;
    }

    public String getXmlns() {
        return xmlns;
    }

    public String getXmlnsurl() {
        return xmlnsurl;
    }

    public String getReferential() {
        return referential;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getUser() {
        return user;
    }

    public String getRegtoppVersion() {
        return regtoppVersion;
    }

    public String getRegtoppCoordinateProjection() {
        return regtoppCoordinateProjection;
    }

    public String getRegtoppCalendarStrategy() {
        return regtoppCalendarStrategy;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public boolean isEnableValidation() {
        return enableValidation;
    }

    public boolean isAllowCreateMissingStopPlace() {
        return allowCreateMissingStopPlace;
    }

    public boolean isEnableStopPlaceIdMapping() {
        return enableStopPlaceIdMapping;
    }

    public boolean isEnableCleanImport() {
        return enableCleanImport;
    }

    public boolean isEnableAutoImport() {
        return enableAutoImport;
    }

    public Set<String> getGenerateMissingServiceLinksForModes() {
        return generateMissingServiceLinksForModes;
    }

    public boolean isGenerateDatedServiceJourneyIds() {
        return generateDatedServiceJourneyIds;
    }

    public boolean isIdfm() {
        return idfm;
    }

    public void setIdfm(boolean idfm) {
        this.idfm = idfm;
    }

    public String getCodeIdfm() {
        return codeIdfm;
    }

    public void setCodeIdfm(String codeIdfm) {
        this.codeIdfm = codeIdfm;
    }
}
