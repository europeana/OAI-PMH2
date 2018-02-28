package eu.europeana.oaipmh.service.exception;

/**
 * The error codes that are defined in OAI-PMH (see also https://www.openarchives.org/OAI/openarchivesprotocol.html#ErrorConditions)
 * @author Patrick Ehlert
 * Created on 27-02-2018
 */
public enum ErrorCode {

    BAD_ARGUMENT("badArgument"), BAD_RESUMPTION_TOKEN("badResumptionToken"), BAD_VERB("badVerb"), CANNOT_DISSEMINATE_FORMAT("cannotDisseminateFormat"),
    ID_DOES_NOT_EXIST("idDoesNotExist"), NO_RECORDS_MATCH("noRecordsMatch"), NO_METADATA_FORMATS("noMetadataFormats"), NO_SET_HIERARCHY("noSetHierarchy");

    private final String code;

    private ErrorCode(String code) {
        this.code = code;
    }

    public String toString() {
        return this.code;
    }
}
