package tr.com.trs.extension.authenticator.usernamepassword;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Collections;
import java.util.List;

import static org.keycloak.provider.ProviderConfigProperty.STRING_TYPE;

@Slf4j
public class UsernamePasswordFactory implements AuthenticatorFactory {

    public static final String ID = "usernamePassword";

    private static final Authenticator AUTHENTICATOR_INSTANCE = new CustomUsernamePasswordRecaptcha();

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return AUTHENTICATOR_INSTANCE;
    }

    @Override
    public String getDisplayType() {
        return "Custom Username Password Authenticator+GoogleCaptcha";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }



    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{AuthenticationExecutionModel.Requirement.REQUIRED};
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Username Password Authentication After Convert Message";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        ProviderConfigProperty name = new ProviderConfigProperty();
        name.setType(STRING_TYPE);
        name.setHelpText(getHelpText());
        return Collections.singletonList(name);
    }

    @Override
    public String getReferenceCategory() {
        return null;
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
        return ID;
    }
}
