package tr.com.trs.extension.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProfileUtil {

    public String getProfile() {
        return System.getenv("KC_PROFILE");
    }

    public boolean isDev() {
        return "dev".equals(getProfile());
    }

    public boolean isStaging() {
        return "staging".equals(getProfile());
    }

    public boolean isMaster() {
        return "prod".equals(getProfile());
    }
}
