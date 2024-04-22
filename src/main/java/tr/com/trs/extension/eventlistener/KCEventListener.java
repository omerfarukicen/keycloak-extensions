package tr.com.trs.extension.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.email.DefaultEmailSenderProvider;
import org.keycloak.email.EmailException;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserModel;
import tr.com.trs.extension.constant.ExtensionsConstants;
import tr.com.trs.extension.util.ProfileUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.keycloak.events.EventType.LOGIN;
import static tr.com.trs.extension.constant.ExtensionsConstants.*;


@Slf4j
public class KCEventListener implements EventListenerProvider {

    public final Set<EventType> eventsTypes = Set.of(LOGIN, EventType.LOGIN_ERROR);


    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private final KeycloakSession keycloakSession;

    private final DefaultEmailSenderProvider senderProvider;
    private final RealmProvider realmProvider;

    public KCEventListener(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
        this.realmProvider = keycloakSession.realms();
        senderProvider = new DefaultEmailSenderProvider(keycloakSession);
    }


    @Override
    public void onEvent(Event event) {
        if (eventsTypes.contains(event.getType()) && event.getType() == LOGIN) {
                 setUserLastLoginTime(event);
        }
    }


    private void setUserLastLoginTime(Event event) {
        if (event.getUserId() != null) {
            RealmModel realm = this.realmProvider.getRealm(event.getRealmId());
            UserModel user = this.keycloakSession.users().getUserById(realm, event.getUserId());
            CompletableFuture.runAsync(() -> {
                log.info("{} kullanıcı login oldu.", user.getUsername());
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                user.setSingleAttribute(USER_SON_GIRIS_TARIHI, dtf.format(LocalDateTime.now()));
                log.info("Updated Time Set: " + user.getId());
            });
        }
    }


    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        if (ResourceType.USER == adminEvent.getResourceType()
                && OperationType.CREATE == adminEvent.getOperationType()) {

            RealmModel realm = this.realmProvider.getRealm(adminEvent.getRealmId());
            String userId = adminEvent.getResourcePath().split("/")[1];
            log.info("UserInfo: " + userId);
            UserModel userModel = this.keycloakSession.users().getUserById(realm, userId);
            log.info("UserModel: " + userModel.getUsername());

            if (userModel.getAttributes().get(USER_TYPE).get(0).equals(PERSONEL_USER)) {
               return;
            }

            Map<String, String> config = keycloakSession.getContext().getRealm().getSmtpConfig();
            String subject = "Registered";
            String message = """
                    Hoşgeldiniz ,
                    Kullanıcınız kimlik sistemine başarılı şekilde kayıt edilmiştir.
                    Kullanıcı adınız: %s
                    Giriş sayfasında bulunan "Forget Password" bölümünden şifrenizi oluşturabilirsiniz.
                    Giriş sayfası: %s
                    """.formatted(userModel.getUsername(), ProfileUtil.isDev() ? ExtensionsConstants.GIRIS_SAYFASI_DEV : ExtensionsConstants.GIRIS_SAYFASI_PAGE);


            String htmlBody = """
                    <html>
                    <head>
                        <style>
                            body {
                                font-family: 'Arial', sans-serif;
                                background-color: #f4f4f4;
                                text-align: center;
                                margin: 50px;
                            }
                            .container {
                                background-color: #fff;
                                padding: 20px;
                                border-radius: 10px;
                                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                                max-width: 600px;
                                margin: 0 auto;
                            }
                            h2 {
                                color: #3498db;
                            }
                            p {
                                color: #555;
                            }
                            a {
                                color: #3498db;
                                text-decoration: none;
                                font-weight: bold;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <h2>Kullanıcı Kaydı Başarılı</h2>
                            <p>Kullanıcı adınız: <strong>%s</strong></p>
                            <p>Şifrenizi oluşturmak için lütfen <a href="%s" target="_blank">giriş sayfasına</a> gidin ve "Şifremi Unuttum" bölümünü kullanın.</p>
                        </div>
                    </body>
                    </html>
                    """.formatted(userModel.getUsername(), ProfileUtil.isStaging() ? ExtensionsConstants.GIRIS_SAYFASI_PAGE : ExtensionsConstants.GIRIS_SAYFASI_CANLI);
            try {
                senderProvider.send(config, userModel.getEmail(), subject, message, htmlBody);
                log.info("Sended Mail.Username: " + userModel.getUsername());
            } catch (EmailException e) {
                log.error("Email Exception: " + e.getMessage());
            }
        }

    }


    @Override
    public void close() {

    }
}
