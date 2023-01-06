package airhacks.lambda.greetings.boundary;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;

import static java.lang.System.Logger.Level.*;

@Path("hello")
@ApplicationScoped
public class GreetingResource {

    static System.Logger LOG = System.getLogger(Greeter.class.getName()); 

    @Inject
    Greeter greeter;

    void onStart(@Observes StartupEvent ev) {
        LOG.log(INFO, "The application is starting with profile " + ProfileManager.getActiveProfile());
      }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        LOG.log(INFO, "GET: **** ");
        return this.greeter.greetings();
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public void hello(String message) {
        LOG.log(INFO, "POST: **** message " + message);
        this.greeter.greetings(message);
    }
}