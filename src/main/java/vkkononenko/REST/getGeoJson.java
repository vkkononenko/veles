package vkkononenko.REST;

import com.sun.javafx.util.Logging;
import vkkononenko.models.Version;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Created by v.kononenko on 30.10.2018.
 */
@Path("/getGeoJson")
@RequestScoped
public class getGeoJson
{
    public static Logger log = Logger.getLogger(getGeoJson.class.getName());

    @PersistenceContext(name = "veles")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public String takeOwnerInfo(@QueryParam("id") Long id) throws IOException {
        try {
            log.info(Objects.toString(id));
            Version version = em.find(Version.class, id);
            return version.getData();
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
        return null;
    }
}

