package org.azmod.zpermissions.events;

import com.avrix.events.OnPlayerConnectEvent;
import org.azmod.zpermissions.Main;
import zombie.core.raknet.UdpConnection;

import java.nio.ByteBuffer;

/**
 * Event handler'OnServerInitializeEvent'
 */
public class OnPlayerJoinHandler extends OnPlayerConnectEvent {

    public OnPlayerJoinHandler(Main m) {
        super();
        main = m;
    }

    private Main main;

    @Override
    public void handleEvent(ByteBuffer byteBuffer, UdpConnection udpConnection, String username) {
        main.permissionManagerUtil.getPlayerPermissions(username);
    }
}
