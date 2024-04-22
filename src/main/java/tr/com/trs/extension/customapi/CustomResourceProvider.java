package tr.com.trs.extension.customapi;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.ClientConnection;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.resource.RealmResourceProvider;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class CustomResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;
    protected ClientConnection clientConnection;
    protected HttpHeaders headers;

    @Override
    public Object getResource() {
        return this;
    }

    @GET
    @Path("logout/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logoutUser(@PathParam("userId") String userId) {
        log.info("Gateway tarafından logout isteği yapıldı.UserID: " + userId);
        RealmModel realmModel = session.getContext().getRealm();
        UserModel user = this.session.users().getUserById(realmModel, userId);
        if (user != null) {
//            session.sessions().getUserSessionsStream(realmModel, user).parallel()
//                    .forEach(userSession -> AuthenticationManager.backchannelLogout(session, realmModel, userSession,
//                            session.getContext().getUri(), clientConnection, headers, true));
            this.session.sessions().getUserSessionsStream(realmModel, user)
                    .forEach(userSession -> AuthenticationManager.backchannelLogout(session, realmModel, userSession,
                            session.getContext().getUri(), clientConnection, headers, true));
        } else {
            log.error("User bulunamadı! UserId: " + userId);
        }
        return Response.ok(Map.of(userId, "User logout edildi")).build();
    }

    @Override
    public void close() {
    }

}