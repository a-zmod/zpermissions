package org.azmod.zpermissions;

public interface Permissible {

    String getPermission();

    String getGrantor();

    boolean allow();

}
