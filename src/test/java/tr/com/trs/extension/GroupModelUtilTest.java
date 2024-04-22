package tr.com.trs.extension;

import org.junit.jupiter.api.Test;
import org.keycloak.models.GroupModel;
import static org.mockito.Mockito.mock;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static tr.com.trs.extension.util.GroupModelUtil.getGroupsWithAttributes;

class GroupModelUtilTest {

    @Test
    void testGetGroupsWithAttributes() {
        // Create a mock GroupModel object
        GroupModel groupModel = mock(GroupModel.class);
        when(groupModel.getName()).thenReturn("group1");
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("attribute1", Arrays.asList("value1"));
        attributes.put("parent_id", Arrays.asList("uuid"));
        attributes.put("id", Arrays.asList("uuid"));
        when(groupModel.getAttributes()).thenReturn(attributes);

        // Create a mock Iterable object
        Iterable<GroupModel> groups = Arrays.asList(groupModel);

        // Call the method and verify the output
        Map<String, Map<String, String>> expectedOutput = new HashMap<>();
        Map<String, String> attribute = new HashMap<>();
        attribute.put("attribute1", "value1");
        attribute.put("parent_id", "uuid");
        attribute.put("id", "uuid");
        expectedOutput.put("group1", attribute);
        assertEquals(expectedOutput, getGroupsWithAttributes(groups));

    }

}
