package tr.com.trs.extension.authenticator.customotp;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.browser.OTPFormAuthenticator;
import org.keycloak.models.*;

import java.util.Map;
import java.util.Optional;

import static org.keycloak.authentication.authenticators.browser.ConditionalOtpFormAuthenticator.OTP_CONTROL_USER_ATTRIBUTE;
import static org.keycloak.authentication.authenticators.browser.ConditionalOtpFormAuthenticator.SKIP_OTP_ROLE;
import static org.keycloak.models.utils.KeycloakModelUtils.getRoleFromString;

@Slf4j
public class ConditionalOtpAuthenticator extends OTPFormAuthenticator {
    public RoleModel istisnaRol;

    enum OtpDecision {
        SKIP_OTP, SHOW_OTP
    }


    @Override
    public void authenticate(AuthenticationFlowContext context) {
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();
        if (tryConcludeBasedOn(voteForUserRole(context.getRealm(), context.getUser(), config), context)) {
            return;
        }
        if (tryConcludeBasedOn(voteForUserOtpControlAttribute(context.getUser(), config), context)) {
            return;
        }
        showOtpForm(context);
    }

    private boolean tryConcludeBasedOn(OtpDecision state, AuthenticationFlowContext context) {
        switch (state) {
            case SHOW_OTP:
                showOtpForm(context);
                return true;
            case SKIP_OTP:
                context.success();
                return true;
            default:
                return false;
        }
    }

    private void showOtpForm(AuthenticationFlowContext context) {
        super.authenticate(context);
    }

    private static boolean tryConcludeBasedOn(OtpDecision state) {
        return OtpDecision.SKIP_OTP == state;
    }


    private static OtpDecision voteForUserOtpControlAttribute(UserModel user, Map<String, String> config) {
        if (!config.containsKey(OTP_CONTROL_USER_ATTRIBUTE)) {
            return OtpDecision.SHOW_OTP;
        }
        String attributeName = config.get(OTP_CONTROL_USER_ATTRIBUTE);
        if (attributeName == null) {
            return OtpDecision.SHOW_OTP;
        }
        Optional<String> value = user.getAttributeStream(attributeName).findFirst();
        return value.map(s -> OtpDecision.SKIP_OTP).orElse(OtpDecision.SHOW_OTP);
    }


    private static OtpDecision voteForUserRole(RealmModel realm, UserModel user, Map<String, String> config) {
        if (config.containsKey(SKIP_OTP_ROLE) && userHasRole(realm, user, config.get(SKIP_OTP_ROLE))) {
            log.info("Kullanıcı otp istisnası yetkisi bulunmaktadır. Kullanıcı: {}", user.getUsername());
            return OtpDecision.SKIP_OTP;
        }
        log.info("Kullanıcı otp zorunludur . Kullanıcı: {}", user.getUsername());
        return OtpDecision.SHOW_OTP;
    }

    private static boolean userHasRole(RealmModel realm, UserModel user, String roleName) {
        RoleModel role = getRoleFromString(realm, roleName);
        log.info("ROLE: " + role.getName());
        return role != null && user.hasRole(role);
    }

    private static boolean isOTPNotRequired(RealmModel realm, UserModel user) {
        return realm.getAuthenticatorConfigsStream().anyMatch((AuthenticatorConfigModel configModel) -> {
            boolean skipOTPUserBase = tryConcludeBasedOn(voteForUserOtpControlAttribute(user, configModel.getConfig()));
            boolean skipOTPUserRoleBase = tryConcludeBasedOn(voteForUserRole(realm, user, configModel.getConfig()));
            return skipOTPUserBase || skipOTPUserRoleBase;
        });
    }


    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        if (isOTPNotRequired(realm, user)) {
            log.info("Kullanıcıya OTP istisna rolü olduğu için OTP kaldırıldı.Kullanıcı: {}", user.getUsername());
            user.removeRequiredAction(UserModel.RequiredAction.CONFIGURE_TOTP);
        } else if (user.getRequiredActionsStream().noneMatch(UserModel.RequiredAction.CONFIGURE_TOTP.name()::equals)) {
            log.info("Kullanıcıya OTP zorunlu hale getirildi.Kullanıcı: {}", user.getUsername());
            user.addRequiredAction(UserModel.RequiredAction.CONFIGURE_TOTP.name());
        }
    }


    //TODO  Cache Yapıları düşünülebilir
    private RoleModel getIstisnaRol(RealmModel realmModel) {
        if (istisnaRol != null) {
            log.info("CACHE");
            return istisnaRol;
        } else {
            log.info("NOT CACHE RealmModel:" + realmModel.getId() + "ROL:" + SKIP_OTP_ROLE);
            RoleModel roleModel = getRoleFromString(realmModel, "kimlik." + SKIP_OTP_ROLE);
            log.info("Role Model" + roleModel.getId());
            return roleModel;
        }
    }
}
