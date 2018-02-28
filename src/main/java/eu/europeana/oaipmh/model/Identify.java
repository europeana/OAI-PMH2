package eu.europeana.oaipmh.model;

import java.io.Serializable;

/**
 * Container for identify data
 * @author Patrick Ehlert
 * Created on 27-02-2018
 */
public class Identify implements Serializable {

    private static final long serialVersionUID = 203469625750930136L;

    // TODO implement rest of the fields

    // required fields
    private String repositoryName = "Europeana Repository";
    private String baseUrl;
    private String protocolVersion;
    private String earliestDateStamp = "2013-02-15T13:04:50Z"; // TODO check if this is the case, right now it's copied from the old project settings
    private String deletedRecord = "no";
    private String granularity;
    private String adminEmail = "api@europeana.eu";

    // optional fields
    private String[] compression;
    private String[] description;

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getEarliestDateStamp() {
        return earliestDateStamp;
    }

    public String getDeletedRecord() {
        return deletedRecord;
    }

    public String getGranularity() {
        return granularity;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String[] getCompression() {
        return compression;
    }

    public String[] getDescription() {
        return description;
    }
}
