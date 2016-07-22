package info.doseamigos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.inject.Guice;
import info.doseamigos.amigousers.AmigoUserGuiceModule;
import info.doseamigos.authusers.AuthUserGuiceModule;
import info.doseamigos.doseevents.DoseEventsGuiceModule;
import info.doseamigos.doseseries.DoseSeriesGuiceModule;
import info.doseamigos.meds.MedGuiceModule;
import info.doseamigos.sharerequests.ShareRequestGuiceModule;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;

/**
 * Testing a crazy theory
 */
public class JerseyApplication extends ResourceConfig {

    @Inject
    public JerseyApplication(@Context ServletContext servletContext, ServiceLocator serviceLocator) {
        register(JacksonJaxbJsonProvider.class);
        register(ObjectMapperResolver.class);

        // Then, enable the bridge between jersey and guice.
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(Guice.createInjector(
            new CommonModule(),
            new AmigoUserGuiceModule(),
            new AuthUserGuiceModule(),
            new DoseEventsGuiceModule(),
            new DoseSeriesGuiceModule(),
            new MedGuiceModule(),
            new ShareRequestGuiceModule()
        ));
    }

    private static class ObjectMapperResolver implements ContextResolver<ObjectMapper> {

        @Override
        public ObjectMapper getContext(Class<?> type) {
            return new ObjectMapperProvider().get();
        }
    }
}
