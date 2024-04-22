package tr.com.trs.extension.custommapper;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;
import java.util.*;
import java.util.stream.Collectors;
import static tr.com.trs.extension.util.GroupModelUtil.getGroupsWithAttributes;

@Slf4j
@RequiredArgsConstructor
public class ActionableGroupsMapper extends AbstractOIDCProtocolMapper
        implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    public static final String PROVIDER_ID = "actionable-group-mapper";

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, GroupsWithAttributesMapper.class);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getDisplayType() {
        return "TS Groups that the user can make a action";
    }

    @Override
    public String getHelpText() {
        return "List of the groups of the user who can do action in groups";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession, KeycloakSession keycloakSession, ClientSessionContext clientSessionCtx) {
        Map<String, Map<String, String>> islemGruplari;
        var userAttributes = userSession.getUser().getAttributes();
        var userGroups = userSession.getUser().getGroupsStream().collect(Collectors.toCollection(HashSet::new));

        if (userAttributes.containsKey("KULLANICIGRUPISLEMTIPI")) {
            var actionGroupType = userAttributes.get("KULLANICIGRUPISLEMTIPI");

            if (actionGroupType.contains("AIDIYET_GRUP")) {
                islemGruplari = getGroupsWithAttributes(userGroups);
            } else if (actionGroupType.contains("ISLEM_GRUP_AIDIYET_GRUP")) {
                var listOfActionGroups = getActionGroupsOfUser(userSession, keycloakSession);
                userGroups.addAll(listOfActionGroups);
                islemGruplari = getGroupsWithAttributes(userGroups);
            } else {
                islemGruplari = getGroupsWithAttributes(getActionGroupsOfUser(userSession, keycloakSession));
            }
        } else {
            log.error("Kullanıcının KULLANICIGRUPISLEMTIPI bulunamadı : " + userSession.getUser().getId());
            islemGruplari = getGroupsWithAttributes(getActionGroupsOfUser(userSession, keycloakSession));
        }


        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, islemGruplari);
    }

    public Set<GroupModel> getActionGroupsOfUser(UserSessionModel userSession, KeycloakSession keycloakSession) {
        EntityManager em = keycloakSession.getProvider(JpaConnectionProvider.class).getEntityManager();
        String kullaniciId = userSession.getUser().getId();

        String sql =
                "SELECT organizasyon_id FROM islem_grup_kullanici " +
                        "JOIN islem_grup " +
                        "ON islem_grup_kullanici.islem_grup_id = islem_grup.id " +
                        "JOIN islem_grup_organizasyon " +
                        "ON islem_grup.id = islem_grup_organizasyon.islem_grup_id " +
                        "WHERE islem_grup_kullanici.kullanici_id = :kullaniciId";

        List<String> groupsIdsOfUser = em.createNativeQuery(sql)
                .setParameter("kullaniciId", kullaniciId)
                .getResultList();

        return groupsIdsOfUser.stream()
                .map(groupId -> keycloakSession.groups().getGroupById(userSession.getRealm(), groupId))
                .collect(Collectors.toSet());
    }

}