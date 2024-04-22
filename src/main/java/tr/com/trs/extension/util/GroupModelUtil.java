package tr.com.trs.extension.util;

import lombok.experimental.UtilityClass;
import org.keycloak.models.GroupModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class GroupModelUtil {

    public static Map<String, Map<String, String>> getGroupsWithAttributes(Iterable<GroupModel> groups) {

        Map<String, Map<String, String>> groupsWithAttributes = new HashMap<>();

        for (GroupModel groupModel : groups) {
            Map<String, String> attribute = new HashMap<>();
            attribute.put("id",groupModel.getId());
            attribute.put("parent_id",groupModel.getParentId());
            for (Map.Entry<String, List<String>> entry : groupModel.getAttributes().entrySet()) {
                attribute.put(entry.getKey(), entry.getValue().get(0));
            }
            groupsWithAttributes.put(groupModel.getName(), attribute);
        }
        return groupsWithAttributes;
    }
}
