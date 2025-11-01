package app.routes;

import app.services.GuideService;
import app.services.TripService;
import io.javalin.apibuilder.EndpointGroup;
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
public class Routes {

    private TripRoutes tripRoutes = new TripRoutes();
    private GuideRoutes guideRoutes = new GuideRoutes();

    public EndpointGroup getRoutes(){
        return () -> {
                get("/", ctx -> ctx.result("Hello from the Trip api"));


                path("/trips", tripRoutes.getTripRoutes());
                path("/guides", guideRoutes.getGuideRoutes());
        };
    }

}
