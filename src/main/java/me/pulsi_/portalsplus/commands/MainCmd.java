package me.pulsi_.portalsplus.commands;

import me.pulsi_.portalsplus.PortalsPlus;
import me.pulsi_.portalsplus.enums.Guis;
import me.pulsi_.portalsplus.getters.PlayersGetter;
import me.pulsi_.portalsplus.managers.GuisManager;
import me.pulsi_.portalsplus.managers.PortalsManager;
import me.pulsi_.portalsplus.objects.PSPlayer;
import me.pulsi_.portalsplus.objects.Portal;
import me.pulsi_.portalsplus.objects.PortalEditor;
import me.pulsi_.portalsplus.utils.PSChat;
import me.pulsi_.portalsplus.utils.PSMessages;
import me.pulsi_.portalsplus.utils.PSMethods;
import me.pulsi_.portalsplus.values.Values;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MainCmd implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender s, Command command, String label, String[] args) {

        if (args.length == 0) {
            PSMessages.send(s, PSChat.prefix + " &7Running on &6v" + PortalsPlus.INSTANCE.getDescription().getVersion() + "&7, made by &6Pulsi_", true);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "create": {
                if (!PSMethods.isPlayer(s) || !PSMethods.hasPermission(s, "portalsplus.create")) return false;

                Player p = (Player) s;
                PSPlayer player = new PlayersGetter(p).getPSPlayer();
                PortalEditor editor = new PortalEditor();

                if (editor.isEditingPortal()) {
                    PSMessages.send(s, "ALREADY_EDITING_PORTAL");
                    return false;
                }

                if (args.length == 1) {
                    PSMessages.send(s, "SPECIFY_NAME");
                    return false;
                }

                String portalName = args[1];
                PortalsManager manager = new PortalsManager(portalName);

                if (manager.exists()) {
                    PSMessages.send(s, "PORTAL_ALREADY_EXIST");
                    return false;
                }

                Portal portal = new Portal(portalName);
                portal.setParticles(Values.CONFIG.getDefaultPortalParticles());

                FileConfiguration portalConfig = portal.getPortalConfig();
                portalConfig.set("particles", Values.CONFIG.getDefaultPortalParticles());
                portalConfig.set("linked-portal", "undefined");
                portalConfig.set("teleport-locations", "[]");
                portalConfig.set("destination", "undefined");
                portal.savePortalFile();

                PortalsPlus.INSTANCE.getPortals().put(portalName, portal);

                editor.setEditingPortal(portal);
                editor.setEditingPortal(true);
                player.setPortalEditor(editor);

                new GuisManager(Guis.CREATOR_GUI).openGui(p);

                PSMessages.send(p, "PORTAL_REGISTERED", "%portal%$" + portalName);
            }
            break;

            case "delete": {
                if (!PSMethods.hasPermission(s, "portalsplus.delete")) return false;

                if (args.length == 1) {
                    PSMessages.send(s, "SPECIFY_NAME");
                    return false;
                }

                String portalName = args[1];
                PortalsManager manager = new PortalsManager(portalName);

                if (!manager.exists()) {
                    PSMessages.send(s, "PORTAL_DOES_NOT_EXIST");
                    return false;
                }

                manager.delete();
                PSMessages.send(s, "PORTAL_DELETED", "%portal%$" + portalName);
            }
            break;

            case "edit": {
                if (!PSMethods.hasPermission(s, "portalsplus.edit")) return false;

                Player p = (Player) s;
                PSPlayer player = new PlayersGetter(p).getPSPlayer();
                PortalEditor editor = new PortalEditor();

                if (editor.isEditingPortal()) {
                    PSMessages.send(s, "ALREADY_EDITING_PORTAL");
                    return false;
                }

                if (args.length == 1) {
                    PSMessages.send(s, "SPECIFY_NAME");
                    return false;
                }

                String portalName = args[1];
                PortalsManager manager = new PortalsManager(portalName);

                if (!manager.exists()) {
                    PSMessages.send(s, "PORTAL_DOES_NOT_EXIST");
                    return false;
                }

                editor.setEditingPortal(manager.getPortal());
                editor.setEditingPortal(true);
                player.setPortalEditor(editor);

                new GuisManager(Guis.CREATOR_GUI).openGui(p);
            }
            break;

            case "link": {
                if (!PSMethods.hasPermission(s, "portalsplus.link")) return false;

                if (args.length == 1) {
                    PSMessages.send(s, "SPECIFY_NAME");
                    return false;
                }

                String portalName1 = args[1];
                PortalsManager manager1 = new PortalsManager(portalName1);

                if (!manager1.exists()) {
                    PSMessages.send(s, "PORTAL_DOES_NOT_EXIST");
                    return false;
                }

                if (args.length == 2) {
                    PSMessages.send(s, "SPECIFY_NAME");
                    return false;
                }

                String portalName2 = args[2];
                PortalsManager manager2 = new PortalsManager(portalName2);

                if (!manager2.exists()) {
                    PSMessages.send(s, "PORTAL_DOES_NOT_EXIST");
                    return false;
                }
                Portal portal1 = manager1.getPortal(), portal2 = manager2.getPortal();

                portal1.setLinkedPortal(portal2);
                portal1.setLinked(true);
                portal2.setLinkedPortal(portal1);
                portal2.setLinked(true);

                portal1.getPortalConfig().set("linked-portal", portal2.getIdentifier());
                portal2.getPortalConfig().set("linked-portal", portal1.getIdentifier());

                portal1.savePortalFile();
                portal2.savePortalFile();

                PSMessages.send(s, "PORTAL_LINKED", "%portal1%$" + portal1.getIdentifier(), "%portal2%$" + portal2.getIdentifier());
            }
            break;

            default:
                PSMessages.send(s, "UNKNOWN_COMMAND");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command command, String alias, String[] args) {

        switch (args.length) {
            case 1: {
                List<String> listOfArgs1 = new ArrayList<>();
                if (s.hasPermission("portalsplus.create")) listOfArgs1.add("create");
                if (s.hasPermission("portalsplus.delete")) listOfArgs1.add("delete");
                if (s.hasPermission("portalsplus.edit")) listOfArgs1.add("edit");
                if (s.hasPermission("portalsplus.link")) listOfArgs1.add("link");

                List<String> args1 = new ArrayList<>();
                for (String a : listOfArgs1) {
                    if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                        args1.add(a);
                }
                return args1;
            }

            case 2: {
                switch (args[0].toLowerCase()) {
                    case "delete":
                    case "edit":
                    case "link": {
                        List<String> args2 = new ArrayList<>();
                        for (String a : new PortalsManager().getPortals()) {
                            if (a.toLowerCase().startsWith(args[1].toLowerCase()))
                                args2.add(a);
                        }
                        return args2;
                    }
                }
            }

            case 3: {
                switch (args[0].toLowerCase()) {
                    case "link": {
                        List<String> args3 = new ArrayList<>();

                        List<String> portals = new ArrayList<>();
                        for (String portal : new PortalsManager().getPortals())
                            if (!args[1].equals(portal)) portals.add(portal);

                        for (String a : portals) {
                            if (a.toLowerCase().startsWith(args[2].toLowerCase()))
                                args3.add(a);
                        }
                        return args3;
                    }
                }
            }
        }
        return null;
    }
}