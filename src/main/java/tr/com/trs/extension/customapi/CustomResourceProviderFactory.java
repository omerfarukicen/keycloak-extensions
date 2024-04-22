package tr.com.trs.extension.customapi;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@AutoService(RealmResourceProviderFactory.class)
@Slf4j
public class CustomResourceProviderFactory implements RealmResourceProviderFactory {

    public static final String API_URL = "custom-connect-api";


    @Override
    public RealmResourceProvider create(KeycloakSession keycloakSession) {
        return new CustomResourceProvider(keycloakSession);
    }

    @Override
    public void init(Config.Scope scope) {
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return API_URL;
    }
}