/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tr.com.trs.extension.authenticator.customotp;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.credential.OTPCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

import static java.util.Arrays.asList;
import static org.keycloak.authentication.authenticators.browser.ConditionalOtpFormAuthenticator.OTP_CONTROL_USER_ATTRIBUTE;
import static org.keycloak.authentication.authenticators.browser.ConditionalOtpFormAuthenticator.SKIP_OTP_ROLE;
import static org.keycloak.provider.ProviderConfigProperty.ROLE_TYPE;
import static org.keycloak.provider.ProviderConfigProperty.STRING_TYPE;

@Slf4j
public class ConditionalOtpAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "ts-conditional-otp-form";
    public static final tr.com.trs.extension.authenticator.customotp.ConditionalOtpAuthenticator SINGLETON = new ConditionalOtpAuthenticator();

    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getReferenceCategory() {
        return OTPCredentialModel.TYPE;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }


    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public String getDisplayType() {
        return "TS-Conditional OTP Form";
    }

    @Override
    public String getHelpText() {
        return "OTP Kontrolü yapar . UserAttribute ve UserRol ile istisnai durumlar ele alınabilir.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        var skipOtpAttribute = new ProviderConfigProperty();
        skipOtpAttribute.setType(STRING_TYPE);
        skipOtpAttribute.setName(OTP_CONTROL_USER_ATTRIBUTE);
        skipOtpAttribute.setLabel("OTP Istisnası User Attribute Bazında");
        skipOtpAttribute.setHelpText("User Attribute Key eklenerek OTP istisnası tanımlanabilir");

        var skipOtpRole = new ProviderConfigProperty();
        skipOtpRole.setType(ROLE_TYPE);
        skipOtpRole.setName(SKIP_OTP_ROLE);
        skipOtpRole.setLabel("OTP Istisnası Rol Bazlı");
        skipOtpRole.setHelpText("OTP_ISTISNA_ROL sahip kullanıcı için yapılan işlemdir.");
        return asList(skipOtpAttribute, skipOtpRole);
    }
}
