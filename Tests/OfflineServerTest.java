import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

public class OfflineServerTest {

    @Test
    public void find_ips_addrs() throws IOException {
        InetAddress localhost = null;
            localhost = InetAddress.getLocalHost();
            // this code assumes IPv4 is used
            byte[] ip = localhost.getAddress();
            for (int i = 1; i <= 254; i++) {
                for (int j = 1; j <= 254; j++) {
                ip[3] = (byte) i;
                InetAddress address = InetAddress.getByAddress(ip);

                if (address.isAnyLocalAddress())
                    if (address.isReachable(1000)) {
                        System.out.println(address + " machine is turned on and can be pinged");
                    }
            }
    }
    }
}
