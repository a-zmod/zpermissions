package org.azmod.zpermissions.commands;

import com.avrix.commands.*;
import com.avrix.enums.AccessLevel;
import com.avrix.enums.CommandScope;
import com.avrix.utils.ChatUtils;
import org.azmod.zpermissions.Permissible;
import org.azmod.zpermissions.Main;
import zombie.core.raknet.UdpConnection;

import java.util.ArrayList;

@CommandName("perms")
@CommandAccessLevel(AccessLevel.ADMIN)
@CommandExecutionScope(CommandScope.BOTH)
@CommandDescription("/perms <add/remove/check/list> <user> <permission>")
public class Perms extends Command {
    private Main main;


    public Perms(Main m){
        super();
        main = m;
    }

    void msg(UdpConnection udp, String msg) {
        if(udp == null) {
            System.out.println(msg);
        } else {
            ChatUtils.sendMessageToPlayer(udp, msg);
        }
    }

    /**
     * Performing a chat command action
     *
     * @param playerConnection {@link UdpConnection}, if called from the console, the connection will return as {@code null}
     * @param args             arguments of the received command
     */
    @Override
    public String onInvoke(UdpConnection playerConnection, String[] args) {
        if(args.length < 1) {
            return "/perms <add/remove/check/list> <user> <permission>";
        }

        String action = args[0];

        String user;
        String permission;

        switch(action) {
            case "add":

                if(args.length < 2) {
                    return "arguments provided does not match command";
                }

                user = args[1];
                permission = args[2];

                boolean granted = main.permissionManagerUtil.grantPermission(permission, user);

                if(granted) {
                    //msg(playerConnection, "Permission: "+permission+" added to user: "+user);
                    return "Permission: "+permission+" added to user: "+user;
                } else {
                   // msg(playerConnection, "Permission: "+permission+"  to user: "+user+" FAILED.");
                    return "Permission: "+permission+"  to user: "+user+" FAILED.";
                }
            case "remove":
                if(args.length < 2) {
                    return "arguments provided does not match command";
                }

                user = args[1];
                permission = args[2];

                boolean removed = main.permissionManagerUtil.removePermission(permission, user);

                if(removed) {
                    return "Permission: "+permission+" removed from user: "+user;
                } else {
                   return "Permission: "+permission+"  remove from user: "+user+" FAILED.";
                }

            case "check":
                if(args.length < 2) {
                    return "arguments provided does not match command";
                }
                user = args[1];
                permission = args[2];

                Permissible permissible = main.permissionManagerUtil.hasPermission(permission, user);

                if(permissible.allow()) {
                    return "Permission: "+permissible.getPermission()+" is granted for user: "+user+" by permission: "+permissible.getGrantor();
                } else {
                    return "Permission: "+permission+"  is not found for: "+user+" FAILED.";
                }

            case "list":
                if(args.length < 1) {
                    return "arguments provided does not match command";
                }

                user = args[1];

                ArrayList<String> permList = main.permissionManagerUtil.getPlayerPermissions(user);

                StringBuilder output = new StringBuilder();

                output.append(user).append("--PERMISSIONS").append('\n');

                for (int i = 0; i < permList.size(); i++) {
                    output.append(permList.get(i)).append('\n');
                }

                String o = output.toString();

                //msg(playerConnection, o);
                return o;
            default:
               // msg(playerConnection, "/perms <add/remove/check/list> <user> <permission>");
                return "/perms <add/remove/check/list> <user> <permission>";
        }

    }
}
