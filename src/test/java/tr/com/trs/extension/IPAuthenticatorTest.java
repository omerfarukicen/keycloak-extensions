package tr.com.trs.extension;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class IPAuthenticatorTest {
    @Test
    void testIpAdressMatch() {
        String ipAdress = "172.15.15.15";
        String remoteIPAddress="172.15.15.15";
        List<String> listIncludeIpAdress = Arrays.asList(
                (ipAdress.split(",")));
        boolean matchIpAddress = listIncludeIpAdress.stream().parallel().anyMatch(x -> x.equals(remoteIPAddress));
        Assertions.assertTrue(matchIpAddress);
    }
}
