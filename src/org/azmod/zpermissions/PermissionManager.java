package org.azmod.zpermissions;

import com.avrix.plugin.Plugin;
import zombie.characters.IsoPlayer;

import java.sql.Connection;
import java.util.ArrayList;

public interface PermissionManager {

    Connection getConnection();
    ArrayList<String> getPermissions();

    ArrayList<String> getPluginPermissions(String plugin);

    ArrayList<String> getPluginPermissions(Plugin plugin);

    ArrayList<String> getPlayerPermissions(IsoPlayer player);

    ArrayList<String> getPlayerPermissions(String player);

    Permissible hasPermission(String permission, IsoPlayer player);

    boolean grantPermission(String permission, IsoPlayer player);

    boolean removePermission(String permission, IsoPlayer player);

    Permissible hasPermission(String permission, String player);

    boolean grantPermission(String permission, String player);

    boolean removePermission(String permission, String player);

    boolean isAdmin(IsoPlayer player);

    void registerPluginPermissions(Plugin plugin, ArrayList<String> permissions);

    void registerPluginPermissions(String plugin, ArrayList<String> permissions);

    void clearCache(String player);
}
