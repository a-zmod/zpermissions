package org.azmod.zpermissions.events;

import com.avrix.events.OnServerShutdownEvent;
import org.azmod.zpermissions.Main;

import java.sql.SQLException;

/**
 * Event handler'OnServerInitializeEvent'
 */
public class OnServerStopHandler extends OnServerShutdownEvent {

    public OnServerStopHandler(Main m) {
        super();
        main = m;
    }

    private Main main;
    /**
     * Called Event Handling Method
     */
    @Override
    public void handleEvent() {
        try {
            if(!main.permissionManagerUtil.getConnection().isClosed()) {
                main.permissionManagerUtil.getConnection().close();
            }

            System.out.println("[ZPermissions] Closed permissions sql connection");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
