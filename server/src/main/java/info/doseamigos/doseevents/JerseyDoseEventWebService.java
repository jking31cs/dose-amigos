package info.doseamigos.doseevents;

import info.doseamigos.authusers.AuthUser;
import info.doseamigos.authusers.AuthUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by jking31cs on 7/21/16.
 */
@Path("dose-events")
public class JerseyDoseEventWebService {

    private static final Logger LOG = LoggerFactory.getLogger(JerseyDoseEventWebService.class);

    private final DoseEventService service;
    private final AuthUserService authUserService;

    @Inject
    public JerseyDoseEventWebService(
        DoseEventService service,
        AuthUserService authUserService
    ) {
        this.service = service;
        this.authUserService = authUserService;
    }

    @Produces("application/json")
    @GET
    public List<DoseEvent> getDoseEvents(
        @QueryParam("idToken") String idToken,
        @QueryParam("dir") String direction,
        @QueryParam("startAt") Date date
    ) throws IOException {
        LOG.info("Starting to grab dose events.");
        try {
            System.out.println("idtoken: " + idToken);
            System.out.println("direction: " + direction);
            if (date == null) {
                date = new Date();
            }
            AuthUser sessionUser = authUserService.getByToken(authUserService.getAccessToken(idToken));
            LOG.info("Grabbed the session user at this point.");
            return service.getDosesForPhone(sessionUser, date, direction);
        } catch (Exception e) {
            LOG.error("Something went Wrong", e);
            throw new RuntimeException(e);
        } finally {
            LOG.info("Finished.");
        }
    }
}
