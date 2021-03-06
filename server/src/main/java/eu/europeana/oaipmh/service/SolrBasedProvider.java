package eu.europeana.oaipmh.service;

import eu.europeana.metis.utils.ExternalRequestUtil;
import eu.europeana.oaipmh.profile.TrackTime;
import eu.europeana.oaipmh.service.exception.BadArgumentException;
import eu.europeana.oaipmh.service.exception.ErrorCode;
import eu.europeana.oaipmh.service.exception.InternalServerErrorException;
import eu.europeana.oaipmh.service.exception.OaiPmhException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrException;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;

public class SolrBasedProvider extends BaseProvider implements ClosableProvider {
    private static final Logger LOG = LogManager.getLogger(SolrBasedProvider.class);

    @Value("${resumptionTokenTTL}")
    private int resumptionTokenTTL;

    @Value("${solr.url}")
    private String solrUrl;

    @Value("${zookeeper.url}")
    private String zookeeperURL;

    @Value("${solr.core}")
    private String solrCore;

    private CloudSolrClient client;

    /**
     * Initialize connection to Solr instance.
     */
    @PostConstruct
    private void init() throws InternalServerErrorException {
        LOG.info("Connecting to Solr cluster: {}", solrUrl);
        LBHttpSolrClient lbTarget;
        try {
            lbTarget = new LBHttpSolrClient(solrUrl.split(","));
        } catch (MalformedURLException e) {
            LOG.error("Solr cluster is not constructed!", e);
            throw new InternalServerErrorException(e.getMessage());
        }

        LOG.info("Setting up Zookeeper {}", zookeeperURL);
        client = new CloudSolrClient.Builder().withZkHost(zookeeperURL).withLBHttpSolrClient(lbTarget).build();
        client.setDefaultCollection(solrCore);
        client.connect();
        LOG.info("Connected to Solr {}", solrUrl);
    }

    @TrackTime
    protected QueryResponse executeQuery(SolrQuery query) throws OaiPmhException {
        try {
            return ExternalRequestUtil.retryableExternalRequest(() -> {
                try {
                    return client.query(query);
                }
                catch(SolrException e){
                    LOG.debug("SolrException {}", e.getMessage());
                    throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, truncateExceptionMessage(e.getMessage()));
                }
                catch (SolrServerException e) {
                    throw new RuntimeException(e);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        catch(SolrException e){
            throw new BadArgumentException(e.getMessage());
        }
        catch (RuntimeException e) {
            throw new OaiPmhException(e.getMessage(), ErrorCode.INTERNAL_ERROR );
        }
    }

    int getResumptionTokenTTL() {
        return resumptionTokenTTL;
    }

    @Override
    public void close() {
        try {
            LOG.info("Destroying Solr client...");
            this.client.close();
        } catch (IOException e) {
            LOG.error("Solr client could not be closed.", e);
        }
    }

    // to truncate the error message thrown by Solr
    private String truncateExceptionMessage(String message){
        String errorMessage;
        if(StringUtils.contains(message , "org.apache.solr.search.SyntaxError")){
            errorMessage = StringUtils.substring(message, StringUtils.indexOf(message, "SyntaxError"), StringUtils.indexOf(message, "at line"));
        } else {
            errorMessage = "Parameter provided is invalid";
        }
        return errorMessage;
    }
}
