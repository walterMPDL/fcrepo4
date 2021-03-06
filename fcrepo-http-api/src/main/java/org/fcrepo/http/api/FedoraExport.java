/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.http.api;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.fcrepo.http.commons.AbstractResource;
import org.fcrepo.serialization.FedoraObjectSerializer;
import org.fcrepo.serialization.SerializerUtil;
import org.fcrepo.http.commons.session.InjectedSession;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Serialization for nodes
 */
@Component
@Scope("prototype")
@Path("/{path: .*}/fcr:export")
public class FedoraExport extends AbstractResource {

    @Autowired
    protected SerializerUtil serializers;

    @InjectedSession
    protected Session session;

    private final Logger logger = getLogger(this.getClass());

    /**
     * Export an object with the given format, e.g.:
     *
     * GET /path/to/object/fcr:export?format=jcr/xml
     * -> the node as JCR/XML
     *
     * @param pathList
     * @param format
     * @return
     */
    @GET
    public Response exportObject(@PathParam("path")
            final List<PathSegment> pathList,
            @QueryParam("format")
            @DefaultValue("jcr/xml")
            final String format) {
        final String path = toPath(pathList);

        logger.debug("Requested object serialization for: " + path +
                " using serialization format " + format);

        final FedoraObjectSerializer serializer =
                serializers.getSerializer(format);

        return Response.ok().type(serializer.getMediaType()).entity(
                new StreamingOutput() {

                    @Override
                    public void write(final OutputStream out)
                        throws IOException {

                        try {
                            logger.debug("Selecting from serializer map: " +
                                    serializers);
                            logger.debug("Retrieved serializer for format: " +
                                    format);
                            serializer.serialize(objectService.getObject(
                                    session, path), out);
                            logger.debug("Successfully serialized object: " +
                                    path);
                        } catch (final RepositoryException e) {
                            throw new WebApplicationException(e);
                        } finally {
                            session.logout();
                        }
                    }
                }).build();

    }
}
