package org.azmod.zpermissions;

import com.avrix.commands.CommandsManager;
import com.avrix.events.EventManager;
import com.avrix.plugin.Metadata;
import com.avrix.plugin.Plugin;
import com.avrix.plugin.ServiceManager;
import org.azmod.zpermissions.commands.Perms;
import org.azmod.zpermissions.events.OnPlayerJoinHandler;
import org.azmod.zpermissions.events.OnPlayerQuitHandler;
import org.azmod.zpermissions.events.OnServerStopHandler;

public class Main extends Plugin {
    /**
     * Constructs a new {@link Plugin} with the specified metadata.
     * Metadata is transferred when the plugin is loaded into the game context.
     *
     * @param metadata The {@link Metadata} associated with this plugin.
     */
    public Main(Metadata metadata) {
        super(metadata);


       // Permissible b = permissionManagerUtil.hasPermission("pz.command.teleport.free", "theman");

       // System.out.println(b.getPermission() + ": grantor: "+b.getGrantor()+": "+b.allow());

    }


    public PermissionManager permissionManagerUtil;

    /**
     * Called when the plugin is initialized.
     * <p>
     * Implementing classes should override this method to provide the initialization logic.
     */
    @Override
    public void onInitialize() {
        ServiceManager.register(PermissionManager.class, permissionManagerUtil);

        permissionManagerUtil = new PermissionManagerUtil(this);

        loadDefaultConfig();

        EventManager.addListener(new OnServerStopHandler(this));

        EventManager.addListener(new OnPlayerJoinHandler(this));
        EventManager.addListener(new OnPlayerQuitHandler(this));

        CommandsManager.addCommand(new Perms(this));
    }

    public static void main(String args[]) {
        new Main(null);
    }
}