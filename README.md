Permissions manager for Avrix project zomboid mod loader

public PermissionManager permissionManager = ServiceManager.getService(PermissionManager.class);

boolean granted = permissionManager.grantPermission(String permission, String player)
boolean removed = permissionManager.removePermission(String permission, String player)

Permissible permissible = permissionManager.hasPermission(String checkPermission, String player)

public interface Permissible {

    String getPermission();

    String getGrantor();

    boolean allow();

}


ArrayList<String> getPlayerPermissions(String player)

