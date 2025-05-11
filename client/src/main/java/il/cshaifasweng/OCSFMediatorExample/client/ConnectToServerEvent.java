package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

public class ConnectToServerEvent {
    private final String HostId;
    private final int PortId;
    private final SimpleClient ClientId;

    public ConnectToServerEvent(String HostId, int PortId) {
        this.HostId = HostId;
        this.PortId = PortId;
        try {
            this.ClientId = SimpleClient.getClient(HostId, PortId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHostId() {
        return HostId;
    }

    public int getPortId() {
        return PortId;
    }

    public SimpleClient getClientId(){
        return ClientId;
    }
}
