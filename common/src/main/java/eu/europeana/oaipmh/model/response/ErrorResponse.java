package eu.europeana.oaipmh.model.response;

import eu.europeana.oaipmh.model.OAIError;
import eu.europeana.oaipmh.model.request.OAIRequest;
import eu.europeana.oaipmh.service.exception.OaiPmhException;

import javax.xml.bind.annotation.XmlElement;

public class ErrorResponse extends OAIResponse {
    @XmlElement
    private OAIError error;

    public ErrorResponse() {}

    public ErrorResponse(OaiPmhException exception, OAIRequest request) {
        super(request);
        this.error = new OAIError(exception.getErrorCode(), exception.getMessage());
    }

    public OAIError getError() {
        return error;
    }

    public void setError(OAIError error) {
        this.error = error;
    }
}
