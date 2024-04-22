package tr.com.trs.extension.authenticator.iprestricted;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.*;
import org.keycloak.models.utils.FormMessage;
import java.util.*;


@Slf4j
public class IPAuthenticator implements Authenticator {
    @Override
    public void authenticate(AuthenticationFlowContext context) {

        UserModel user = context.getUser();
        String remoteIPAddress = context.getConnection().getRemoteAddr();
        log.info("remoteIPAddress="+remoteIPAddress);
        Optional<GroupModel> userGroup = context.getUser().getGroupsStream().findFirst();

        if (userGroup.isEmpty()) {
            log.error(user.getUsername() + " kullanıcı bir gruba ait olmak zorunda. Lütfen yöneticinize bildiriniz");
            context.forkWithErrorMessage(new FormMessage("label", "Kullanıcı bir gruba ait olmak zorunda. Lütfen yöneticinize bildiriniz"));
        } else {
            Map<String, List<String>> groupsAttributes = userGroup.get().getAttributes();
            Optional<Map.Entry<String, List<String>>> ipAdressMap = groupsAttributes.entrySet().stream()
                    .filter(attributes -> "IP_ADRES".equals(attributes.getKey())).findAny();

            if (ipAdressMap.isPresent() && !isGroupHaveIPException(userGroup,context)) {
                List<String> listIncludeIpAdress = Arrays.asList((ipAdressMap.get().getValue().get(0).split(",")));

                if (listIncludeIpAdress.contains(remoteIPAddress)) {
                   context.success();
                } else {
                    context.forkWithErrorMessage(new FormMessage("label", "Kullanıcının ip adresi kabul edilebilir değil. Lütfen yöneticinize bildiriniz."));
                }

            } else {
                context.success();
            }

        }
    }

    public boolean isGroupHaveIPException( Optional<GroupModel> group , AuthenticationFlowContext context ){

        if( group.isPresent() ){
            var em = context.getSession().getProvider(JpaConnectionProvider.class).getEntityManager();
            String groupId = group.get().getId();
            String sql = "SELECT COUNT(*) FROM istisna " +
                         "WHERE organizasyon_id =:orgId "+
                         "AND istisna_tipi =:istisnaTipi "+
                         "AND aktif =:isAktif";
            // 0 == IP ENUM VALUE
            return ((Number)em.createNativeQuery(sql)
                    .setParameter("orgId", groupId)
                    .setParameter("istisnaTipi", 0)
                    .setParameter("isAktif",Boolean.TRUE)
                    .getSingleResult()).longValue() > 0;
        }

        return true;
    }

    @Override
    public void action(AuthenticationFlowContext context) {
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {
    }

}
