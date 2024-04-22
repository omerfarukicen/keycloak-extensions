package tr.com.trs.extension.eventlistener;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import tr.com.trs.extension.util.PropertiesReader;


@Slf4j
public class KCEventListenerProvider implements EventListenerProviderFactory {
    private static final String KEYCLOAK_EXTENSION_VERSION;

    static {
        KEYCLOAK_EXTENSION_VERSION = System.getProperty("keycloak.extension.version", "default-value");
    }

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new KCEventListener(keycloakSession);
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    public void close() {

    }

    /* Keycloak sürümleri
     * Atmış olduğumuz sürümün numarasını Keycloak arayüzünden EventListener sürümü üzerinden takip etmekteyiz.
     * Bu yüzden Pom xml keycloak.extension.version dışarıdan setleyip uygulama içinden okumak istiyoruz*
     *  admin/master/console/#/turkiye-sigorta/realm-settings/events
     */
    @Override
    @SneakyThrows
    public String getId() {
        PropertiesReader reader = new PropertiesReader("properties-from-pom.properties");
        return "CustomExtension-V" + reader.getProperty("keycloak.extension.version");
    }


}
