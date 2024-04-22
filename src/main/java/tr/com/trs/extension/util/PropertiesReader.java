package tr.com.trs.extension.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/* Keycloak sürümleri
* Atmış olduğumuz sürümün numarasını Keycloak arayüzünden EventListener sürümü üzerinden takip etmekteyiz.
* Bu yüzden Pom xml keycloak.extension.version dışarıdan setleyip uygulama içinden okumak istiyoruz
* */

public class PropertiesReader {
    private Properties properties;

    public PropertiesReader(String propertyFileName) throws IOException {
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream(propertyFileName);
        this.properties = new Properties();
        this.properties.load(is);
    }

    public String getProperty(String propertyName) {
        return this.properties.getProperty(propertyName);
    }
}