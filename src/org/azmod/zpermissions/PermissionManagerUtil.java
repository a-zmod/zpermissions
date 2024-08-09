package org.azmod.zpermissions;

import com.avrix.enums.AccessLevel;
import com.avrix.plugin.Plugin;
import com.avrix.plugin.ServiceManager;
import com.avrix.utils.PlayerUtils;
import com.brov3r.databaseapi.services.DatabaseAPI;
import zombie.characters.IsoPlayer;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PermissionManagerUtil implements PermissionManager {

    Main main;
    DatabaseAPI databaseAPI;

    protected ArrayList<String> defaultPermissions = new ArrayList<>();

    protected HashMap<String, ArrayList<String>> pluginPermissions = new HashMap<>();

    private File dbFile;

    public Connection connection;

    protected HashMap<String, ArrayList<String>> permissionCache = new HashMap<>();

    public PermissionManagerUtil(Main m) {
        main = m;
        dbFile = new File(main.getConfigFolder().getPath() + "/permissions.db");
        databaseAPI = ServiceManager.getService(DatabaseAPI.class);

        try {
            connection = databaseAPI.getConnection(dbFile);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            validateDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return defaultPermissions;
    }

    @Override
    public ArrayList<String> getPluginPermissions(String plugin) {
        return pluginPermissions.getOrDefault(plugin, null);
    }

    @Override
    public ArrayList<String> getPluginPermissions(Plugin plugin) {
        return getPluginPermissions(plugin.getMetadata().getName());
    }

    @Override
    public ArrayList<String> getPlayerPermissions(IsoPlayer player) {
        return getPlayerPermissions(player.name);
    }

    @Override
    public ArrayList<String> getPlayerPermissions(String player) {
        //get player perms from db and fill in arraylist, add defaults if not present
        if(permissionCache.containsKey(player.toLowerCase())) {
            return permissionCache.get(player.toLowerCase());
        } else {
            try {
                String SQL = "SELECT permission FROM pzpermissions WHERE username like ?";

                PreparedStatement statement = connection.prepareStatement(SQL);

                statement.setString(1, "%"+player+"%");

                ResultSet query = statement.executeQuery();

                ArrayList<String> readPerms = new ArrayList<>();

                while (query.next()) {
                    readPerms.add(query.getString(1));
                }

                statement.close();
                ArrayList<String> output = new ArrayList<>();



                if(readPerms.size() == 0) {
                    //new permission account
                    ArrayList<String> pluginList = new ArrayList<>();

                    if (pluginPermissions.keySet().size() > 0) {
                        pluginList.addAll(pluginPermissions.keySet());
                    }

                    for (int i = 0; i < pluginList.size(); i++) {

                        ArrayList<String> pluginDefaults = pluginPermissions.get(pluginList.get(i));

                        for(int j = 0; j < pluginDefaults.size(); j++) {
                            grantPermission(player, pluginDefaults.get(j));
                        }

                        output.addAll(pluginDefaults);
                    }
                }

                output.addAll(readPerms);

                permissionCache.put(player.toLowerCase(), output);

                return output;



            } catch (Exception e) {
                e.printStackTrace();
            }

            return defaultPermissions;
        }
    }

    @Override
    public Permissible hasPermission(String checkPermission, IsoPlayer player) {
        return hasPermission(checkPermission, player.name);
    }

    @Override
    public boolean grantPermission(String permission, IsoPlayer player) {
        return grantPermission(permission, player.name);
    }

    @Override
    public boolean removePermission(String permission, IsoPlayer player) {
        return removePermission(permission, player.name);
    }

    @Override
    public Permissible hasPermission(String checkPermission, String player) {
        ArrayList<String> permissions = getPlayerPermissions(player);

        if (permissions.contains(checkPermission)) {
            return new Permissible() {
                @Override
                public String getPermission() {
                    return checkPermission;
                }

                @Override
                public String getGrantor() {
                    return checkPermission;
                }

                @Override
                public boolean allow() {
                    return true;
                }
            };
        } else {
            //wildcard
            for (int i = 0; i < permissions.size(); i++) {
                String permission = permissions.get(i);

                String[] nodes = permission.split("\\.");

                String[] checkNode = checkPermission.split("\\.");


                boolean doMatch = false;

                if (permission.endsWith("*")) {
                    int matches = 0;

                    for (int j = 0; j < nodes.length; j++) {
                        if(j >= checkNode.length) {
                            break;
                        }

                        if (nodes[j].equals(checkNode[j]) || nodes[j].equals("*")) {
                            matches++;
                        }

                        if(matches > 0 && nodes[j].equalsIgnoreCase("*")) {
                            doMatch = true;
                            break;
                        }
                    }

                    final int aM = matches;
                    final boolean doM = doMatch;
                    return new Permissible() {
                        @Override
                        public String getPermission() {
                            return checkPermission;
                        }

                        @Override
                        public String getGrantor() {
                            return permission;
                        }

                        @Override
                        public boolean allow() {
                           // System.out.println(aM);
                            return aM == checkNode.length || doM;
                        }
                    };
                }
            }
        }
        return new Permissible() {
            @Override
            public String getPermission() {
                return checkPermission;
            }

            @Override
            public String getGrantor() {
                return null;
            }

            @Override
            public boolean allow() {
                return false;
            }
        };
    }

    @Override
    public boolean grantPermission(String permission, String player) {
        boolean hasPerm = sqlHasPermission(permission, player);

        if(!hasPerm) {
            String SQL = "INSERT INTO pzpermissions(username, permission) VALUES (?, ?);";

            try {
                PreparedStatement statement = connection.prepareStatement(SQL);

                statement.setString(1, player);
                statement.setString(2, permission);

                statement.execute();

                statement.close();

                permissionCache.remove(player.toLowerCase());

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

        }

        return false;

    }

    boolean sqlHasPermission(String permission, String player) {
        String SQL = "SELECT permission FROM pzpermissions WHERE permission = ? AND username like ?";


        try {
            PreparedStatement statement = connection.prepareStatement(SQL);
            statement.setString(1, permission);
            statement.setString(2, "%"+player+"%");

            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                return true;
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    @Override
    public boolean removePermission(String permission, String player) {
        boolean hasPerm = sqlHasPermission(permission, player);

        if(hasPerm) {
            String SQL = "DELETE FROM pzpermissions WHERE permission = ? AND username like ?";

            try {
                PreparedStatement statement = connection.prepareStatement(SQL);

                statement.setString(1, permission);
                statement.setString(2, "%"+player+"%");

                statement.execute();

                statement.close();

                permissionCache.remove(player.toLowerCase());

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

        }

        return false;
    }

    @Override
    public boolean isAdmin(IsoPlayer player) {
        return PlayerUtils.getAccessLevel(player) == AccessLevel.ADMIN;
    }

    @Override
    public void registerPluginPermissions(Plugin plugin, ArrayList<String> permissions) {
        pluginPermissions.put(plugin.getMetadata().getName(), permissions);
    }

    @Override
    public void registerPluginPermissions(String plugin, ArrayList<String> permissions) {
        pluginPermissions.put(plugin, permissions);
    }

    @Override
    public void clearCache(String player) {
        permissionCache.remove(player);
    }

    void validateDatabase() throws SQLException {
        databaseAPI.executeSql(dbFile, "CREATE TABLE IF NOT EXISTS pzpermissions" +
                "(username TEXT," +
                "permission TEXT)");
    }
}
