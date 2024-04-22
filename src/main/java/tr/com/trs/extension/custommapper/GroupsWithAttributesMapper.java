package tr.com.trs.extension.custommapper;

import org.keycloak.models.*;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static tr.com.trs.extension.util.GroupModelUtil.getGroupsWithAttributes;


public class GroupsWithAttributesMapper extends AbstractOIDCProtocolMapper
        implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    public static final String PROVIDER_ID = "groups-with-attributes";
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
        return "Groups With Attributes";
    }

    @Override
    public String getHelpText() {
        return "List of groups with attributes";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession, KeycloakSession keycloakSession, ClientSessionContext clientSessionCtx) {
        Set<GroupModel> groups = userSession.getUser().getGroupsStream().collect(Collectors.toSet());
        Map<String, Map<String,String>> groupsWithAttributes = getGroupsWithAttributes(groups);
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, groupsWithAttributes);
    }
}
