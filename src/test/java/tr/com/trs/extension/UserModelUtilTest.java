package tr.com.trs.extension;

import org.junit.jupiter.api.Test;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static tr.com.trs.extension.util.UserModelUtil.getUserWithAttributes;


class UserModelUtilTest {
    @Test
    void testGetUserWithAttributes(){
        // Create a mock UserSessionModel object
        UserSessionModel userSession = mock(UserSessionModel.class);
        UserModel user = mock(UserModel.class);
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("attribute1", Arrays.asList("value1"));
        when(user.getAttributes()).thenReturn(attributes);
        when(userSession.getUser()).thenReturn(user);

        // Call the method and verify the output
        Map<String, String> expectedOutput = new HashMap<>();
        expectedOutput.put("attribute1", "value1");
        assertEquals(expectedOutput, getUserWithAttributes(userSession));
    }
}
