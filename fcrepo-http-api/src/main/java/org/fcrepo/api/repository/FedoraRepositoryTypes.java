package org.fcrepo.api.repository;

import com.codahale.metrics.annotation.Timed;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.WebContent;
import org.fcrepo.AbstractResource;
import org.fcrepo.FedoraResource;
import org.fcrepo.api.FedoraNodes;
import org.fcrepo.api.rdf.HttpGraphSubjects;
import org.fcrepo.rdf.GraphProperties;
import org.fcrepo.session.InjectedSession;
import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;

import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static javax.ws.rs.core.Response.status;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.fcrepo.http.RDFMediaType.N3;
import static org.fcrepo.http.RDFMediaType.N3_ALT1;
import static org.fcrepo.http.RDFMediaType.N3_ALT2;
import static org.fcrepo.http.RDFMediaType.NTRIPLES;
import static org.fcrepo.http.RDFMediaType.RDF_JSON;
import static org.fcrepo.http.RDFMediaType.RDF_XML;
import static org.fcrepo.http.RDFMediaType.TURTLE;
import static org.slf4j.LoggerFactory.getLogger;

@Component
@Scope("prototype")
@Path("/fcr:nodeTypes")
public class FedoraRepositoryTypes extends AbstractResource {

    @InjectedSession
    protected Session session;

    private static final Logger logger =
        getLogger(FedoraRepositoriesProperties.class);

    /**
     * Get the node types as RDFS
     *
     * @return
     * @throws RepositoryException
     */
    @GET
    @Produces({TURTLE, N3, N3_ALT1, N3_ALT2, RDF_XML, RDF_JSON, NTRIPLES,
                  TEXT_HTML})
    @Timed
    public Dataset getNodeTypes() throws RepositoryException {
        try {
            final HttpGraphSubjects subjects = new HttpGraphSubjects(FedoraNodes.class, uriInfo);
            return nodeService.getNodeTypes(session, subjects);
        } finally {
            session.logout();
        }
    }

    /**
     * Update the NodeTypes by POSTing a CND file/fragment
     *
     * @return 201
     * @throws javax.jcr.RepositoryException
     * @throws java.io.IOException
     */
    @POST
    @Consumes({"text/cnd"})
    @Timed
    public Response updateCnd(final InputStream requestBodyStream)
        throws RepositoryException, IOException {

        try {

            final HttpGraphSubjects subjects = new HttpGraphSubjects(FedoraNodes.class, uriInfo);
            Dataset dataset =
                nodeService.registerNodeTypes(session,
                                                 subjects,
                                                 requestBodyStream);

            if (dataset.containsNamedModel(GraphProperties.PROBLEMS_MODEL_NAME)) {
                Model problems = dataset.getNamedModel(GraphProperties.PROBLEMS_MODEL_NAME);

                logger.info("Found these problems updating"
                                + " the properties for {}: {}",
                               "/",
                               problems.toString());

                return status(Response.Status.FORBIDDEN)
                           .entity(problems.toString()).build();
            }

            session.save();

            return status(SC_NO_CONTENT).build();

        } finally {
            session.logout();
        }
    }
}
