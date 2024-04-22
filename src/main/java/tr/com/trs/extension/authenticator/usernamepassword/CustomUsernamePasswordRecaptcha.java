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

package tr.com.trs.extension.authenticator.usernamepassword;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.services.ServicesLogger;
import org.keycloak.services.validation.Validation;
import org.keycloak.storage.ldap.LDAPConfig;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.LDAPStorageProviderFactory;
import org.keycloak.storage.ldap.idm.store.IdentityStore;
import org.keycloak.storage.ldap.idm.store.ldap.LDAPIdentityStore;
import org.keycloak.util.JsonSerialization;

import javax.naming.AuthenticationException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.keycloak.events.Errors.USER_TEMPORARILY_DISABLED;
import static org.keycloak.models.LDAPConstants.*;
import static tr.com.trs.extension.constant.ExtensionsConstants.*;

@Slf4j
public class CustomUsernamePasswordRecaptcha extends UsernamePasswordForm {
    public static final String G_RECAPTCHA_RESPONSE = "g-recaptcha-response";
    public static final String SITE_KEY = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
    public static final String SITE_SECRET = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX;
    protected static final MultivaluedHashMap<String, String> ldapConfiguration = new MultivaluedHashMap<>();

    static {
        ldapConfiguration.add(USERS_DN, LDAP_USERS_DN);
        ldapConfiguration.add(USER_OBJECT_CLASSES, LDAP_USER_OBJECT_CLASSES);
        ldapConfiguration.add(UUID_LDAP_ATTRIBUTE, LDAP_UUID_LDAP_ATTRIBUTE);
        ldapConfiguration.add(USERNAME_LDAP_ATTRIBUTE, LDAP_USERNAME_LDAP_ATTRIBUTE);
        ldapConfiguration.add(RDN_LDAP_ATTRIBUTE, LDAP_RDN_LDAP_ATTRIBUTE);
        ldapConfiguration.add(CONNECTION_URL, LDAP_CONNECTION_URL);
        ldapConfiguration.add(BIND_CREDENTIAL, LDAP_BIND_CREDENTIAL);
        ldapConfiguration.add(BIND_DN, LDAP_BIND_DN);
    }

    @Override
    protected Response createLoginForm(LoginFormsProvider form) {
        form.setAttribute("recaptchaRequired", false);
        form.setAttribute("recaptchaSiteKey", SITE_KEY);
        return super.createLoginForm(form);
    }


    @Override
    protected boolean validateForm(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {

        if (captchaControl(context, formData)) return false;
        var isValidUser = super.validateForm(context, formData);
        var validUserErrorMessage = context.getEvent().getEvent().getError();
        log.info("validUserErrorMessage: " + validUserErrorMessage);

        if (formData.getFirst("radio").equals("mkysLogin")) {
            if (!isValidUser && USER_TEMPORARILY_DISABLED.equals(validUserErrorMessage)) {
                return bruteForceAttackValideMessage(context, formData);
            }
            if (!isValidUser) {
                return userAndPasswordValidMessage(context, formData);
            }
        }
        if (formData.getFirst("radio").equals("ldapLogin")) {
            log.info("LDAP LOGIN USERNAME: " + formData.getFirst("username"));
            boolean isUserValid =super.validateUser(context, formData);
            if (!isValidUser && USER_TEMPORARILY_DISABLED.equals(validUserErrorMessage)) {
                return bruteForceAttackValideMessage(context, formData);
            }
            if (!isUserValid) {
                return userAndPasswordValidMessage(context, formData);
            }
            var userModel = context.getUser();
            IdentityStore identityStore = new LDAPIdentityStore(context.getSession(), new LDAPConfig(ldapConfiguration));
            LDAPStorageProviderFactory ldapStorageProviderFactory = new LDAPStorageProviderFactory();
            LDAPStorageProvider ldapStorageProvider = new LDAPStorageProvider(ldapStorageProviderFactory, context.getSession(), new ComponentModel(), new LDAPIdentityStore(context.getSession(), new LDAPConfig(ldapConfiguration)));
            var ldapObject = ldapStorageProvider.loadLDAPUserByUsername(context.getRealm(), userModel.getUsername());
            try {
                identityStore.validatePassword(ldapObject, formData.getFirst("password"));
                return true;
            } catch (AuthenticationException ex) {
                return userAndPasswordValidMessage(context, formData);
            } catch (Exception ex) {
                log.error("LDAP LOGIN FAIL: " + ex.getMessage());
                clearForm(context, formData);
                var challengeResponse = this.challenge(context, "LDAP tarafında bir hata ile karşılaşıldı.Lütfen tekrar deneyiniz!");
                context.failureChallenge(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR, challengeResponse);
                return false;
            }
        }
        return true;
    }

    private boolean bruteForceAttackValideMessage(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        log.error(formData.getFirst("username") + " The user has been temporarily locked due to excessive login attempts.");
        context.getEvent().error("brute_force_attack");
        Response challengeResponse = this.challenge(context, "You have made too many login attempts, so your account has been locked for 30 minutes.");
        context.forceChallenge(challengeResponse);
        return false;
    }

    private boolean userAndPasswordValidMessage(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        log.error(formData.getFirst("username") + ":"+WRONG_PASSWORD_AND_USERNAME);
        clearForm(context, formData);
        var challengeResponse = this.challenge(context, WRONG_PASSWORD_AND_USERNAME);
        context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);
        return false;
    }

    private boolean captchaControl(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        if (formData.containsKey(G_RECAPTCHA_RESPONSE)) {
            String captcha = formData.getFirst(G_RECAPTCHA_RESPONSE);
            if (Validation.isBlank(captcha)) {
                clearForm(context, formData);
                Response challengeResponse = this.challenge(context, NO_IMAGE);
                context.forceChallenge(challengeResponse);
                return true;
            }
            boolean validateCaptcha = validateRecaptcha(context, captcha);
            if (!validateCaptcha) {
                log.error("ValidateCaptchaException");
                clearForm(context, formData);
                Response challengeResponse = this.challenge(context, NO_IMAGE);
                context.forceChallenge(challengeResponse);
                return true;
            }
        }
        return false;
    }

    private void clearForm(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        formData.remove(G_RECAPTCHA_RESPONSE);
        createCaptcha(context.form(), context.getSession().getContext().resolveLocale(context.getUser()).toLanguageTag());
    }

    private void createCaptcha(LoginFormsProvider form, String userLanguageTag) {
        form.setAttribute("recaptchaRequired", true);
        form.setAttribute("recaptchaSiteKey", SITE_KEY);
        form.addScript("https://www.google.com/recaptcha/api.js?hl=" + userLanguageTag);
    }


    protected boolean validateRecaptcha(AuthenticationFlowContext context, String captcha) {
        CloseableHttpClient httpClient = context.getSession().getProvider(HttpClientProvider.class).getHttpClient();
        HttpPost post = new HttpPost(VALIDATE_CAPTCHA);
        List<NameValuePair> formParams = new LinkedList<>();
        formParams.add(new BasicNameValuePair("secret", SITE_SECRET));
        formParams.add(new BasicNameValuePair("response", captcha));
        formParams.add(new BasicNameValuePair("remoteip", context.getConnection().getRemoteAddr()));
        try {
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(formParams, "UTF-8");
            post.setEntity(form);
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                InputStream content = response.getEntity().getContent();
                try {
                    var json = JsonSerialization.readValue(content, Map.class);
                    Object val = json.get("success");
                    return Boolean.TRUE.equals(val);
                } finally {
                    EntityUtils.consumeQuietly(response.getEntity());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ServicesLogger.LOGGER.recaptchaFailed(e);
            return false;
        }
    }

}
