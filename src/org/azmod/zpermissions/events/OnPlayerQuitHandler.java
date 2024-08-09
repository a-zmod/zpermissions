package org.azmod.zpermissions.events;

import com.avrix.events.OnPlayerDisconnectEvent;
import org.azmod.zpermissions.Main;
import zombie.characters.IsoPlayer;
import zombie.core.raknet.UdpConnection;

/**
 * Event handler'OnServerInitializeEvent'
 */
public class OnPlayerQuitHandler extends OnPlayerDisconnectEvent {

    public OnPlayerQuitHandler(Main m) {
        super();
        main = m;
    }

    private Main main;
    @Override
    public void handleEvent(IsoPlayer isoPlayer, UdpConnection udpConnection) {
        main.permissionManagerUtil.getPlayerPermissions(isoPlayer.name);
    }
}
