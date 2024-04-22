package tr.com.trs.extension.util;

import lombok.experimental.UtilityClass;
import org.keycloak.models.UserSessionModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class UserModelUtil {

    public static Map<String,String> getUserWithAttributes(UserSessionModel userSession){
        Map<String,String> userWithAttributes = new HashMap<>();

            for (Map.Entry<String, List<String>> entry : userSession.getUser().getAttributes().entrySet() ) {
            String key = entry.getKey();
            String value = entry.getValue().get(0);
            userWithAttributes.put(key,value);
            }

        return userWithAttributes;
    }
}
