package me.qKing12.HandyOrbs;

import me.qKing12.HandyOrbs.Orbs.Orb;
import me.qKing12.HandyOrbs.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, Listener {
    private static Main plugin;

    public Commands(Main plugin) {
        this.plugin = plugin;

        plugin.getCommand("handyorbs").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length==0){
                String permission=plugin.getConfig().getString("open-own-orbs-menu-permission");
                if(permission.equals("none") || p.hasPermission(permission)){
                    PlayerData.ownOrbsMenu(p, p.getUniqueId().toString());
                    return true;
                }
                else {
                    p.sendMessage(utils.chat(plugin.getConfig().getString("no-permission-message")));
                    return false;
                }
            }
            else if(args[0].equalsIgnoreCase("debug") && p.hasPermission("handyorbs.admin")){
                ArrayList<ArmorStand> armorStands = new ArrayList<>();
                for(Entity ent : p.getLocation().getWorld().getNearbyEntities(p.getLocation(), 2, 2, 2)) {
                    if (ent.getType().equals(EntityType.ARMOR_STAND)) {
                        ArmorStand am = (ArmorStand) ent;
                        if(am.isSmall() && !am.isVisible() && am.hasArms())
                            armorStands.add((ArmorStand) ent);
                    }
                }
                for(ArmorStand am : armorStands) {
                    if (am == null)
                        p.sendMessage("No ArmorStand found");
                    else {
                        p.sendMessage("ArmorStand Location: " + am.getLocation().toString());
                        Orb orb = ConfigLoad.getOrbByLocation(am);
                        if (orb == null)
                            p.sendMessage("No orb in memory for this armorstand.");
                        else {
                            p.sendMessage("Orb Location: " + orb.getLocation().toString());
                            orb.debug(p, am);
                        }
                    }
                }
            }
            else if(args[0].equalsIgnoreCase("debugChunk") && p.hasPermission("handyorbs.admin")){
                if(ConfigLoad.orbsManager.containsKey(p.getLocation().getChunk().toString())){
                    p.sendMessage("Chunk is in memory");
                    for(Orb orb : ConfigLoad.orbsManager.get(p.getLocation().getChunk().toString())){
                        p.sendMessage(orb.getLocation().toString());
                    }
                }
                else{
                    p.sendMessage("Chunk is not in memory");
                    for(String chunk : ConfigLoad.orbsManager.keySet())
                        p.sendMessage(chunk);
                }
            }
            else if(args[0].equalsIgnoreCase("cleanUpChunk") && p.hasPermission("handyorbs.admin")){
                if(ConfigLoad.orbsManager.containsKey(p.getLocation().getChunk().toString())){
                    p.sendMessage(utils.chat("&aChunk clean up attempted."));
                    for(Orb orb : ConfigLoad.orbsManager.get(p.getLocation().getChunk().toString())){
                        ArmorStand am = orb.getArmorStand();
                        if(am==null || am.isDead())
                            orb.unload(false);
                    }
                }
                else{
                    p.sendMessage(utils.chat("&cChunk is not in memory"));
                }
            }
                else if(args[0].equalsIgnoreCase("get")){
                    if(p.hasPermission(plugin.getConfig().getString("open-get-menu-permission")))
                        p.openInventory(ConfigLoad.adminInventory);
                    else {
                        p.sendMessage(utils.chat(plugin.getConfig().getString("no-permission-message")));
                        return false;
                    }
                }
                else if(args[0].equalsIgnoreCase("give")){
                    if(p.hasPermission(plugin.getConfig().getString("give-permission"))){
                        if(args.length>=2){
                            Player player = Bukkit.getPlayer(args[1]);
                            if(player!=null){
                                if(args.length>=3){
                                    switch(args[2]){
                                        case "wheat":
                                            if(player.getInventory().firstEmpty()!=-1) {
                                                player.getInventory().addItem(utils.makeUnique(ConfigLoad.farmingWheatOrb));
                                            }
                                            else
                                                player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.farmingWheatOrb));
                                            p.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                            return true;
                                        case "carrots":
                                            if(player.getInventory().firstEmpty()!=-1) {
                                                player.getInventory().addItem(utils.makeUnique(ConfigLoad.farmingCarrotsOrb));
                                            }
                                            else
                                                player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.farmingCarrotsOrb));
                                            p.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                            return true;
                                        case "potatoes":
                                            if(player.getInventory().firstEmpty()!=-1) {
                                                player.getInventory().addItem(utils.makeUnique(ConfigLoad.farmingPotatoesOrb));
                                            }
                                            else
                                                player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.farmingPotatoesOrb));
                                            p.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                            return true;
                                        case "beetroot":
                                            if(!Bukkit.getVersion().contains("1.8")) {
                                                if (player.getInventory().firstEmpty() != -1) {
                                                    player.getInventory().addItem(utils.makeUnique(ConfigLoad.farmingBeetrootOrb));
                                                } else
                                                    player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.farmingBeetrootOrb));
                                                p.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                            }
                                            return true;
                                        case "nether-wart":
                                            if(player.getInventory().firstEmpty()!=-1) {
                                                player.getInventory().addItem(utils.makeUnique(ConfigLoad.netherWartOrb));
                                            }
                                            else
                                                player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.netherWartOrb));
                                            p.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                            return true;
                                        case "sugar-cane":
                                            if(player.getInventory().firstEmpty()!=-1) {
                                                player.getInventory().addItem(utils.makeUnique(ConfigLoad.sugarCaneOrb));
                                            }
                                            else
                                                player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.sugarCaneOrb));
                                            p.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                            return true;
                                        case "fishing":
                                            if(player.getInventory().firstEmpty()!=-1) {
                                                player.getInventory().addItem(utils.makeUnique(ConfigLoad.fishingOrb));
                                            }
                                            else
                                                player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.fishingOrb));
                                            p.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                            return true;
                                        case "flower":
                                            if(player.getInventory().firstEmpty()!=-1) {
                                                player.getInventory().addItem(utils.makeUnique(ConfigLoad.flowerOrb));
                                            }
                                            else
                                                player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.flowerOrb));
                                            p.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                            return true;
                                        case "rainbow":
                                            if(player.getInventory().firstEmpty()!=-1) {
                                                player.getInventory().addItem(utils.makeUnique(ConfigLoad.rainbowOrb));
                                            }
                                            else
                                                player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.rainbowOrb));
                                            p.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                            return true;
                                        case "radiant":
                                            if(player.getInventory().firstEmpty()!=-1) {
                                                player.getInventory().addItem(utils.makeUnique(ConfigLoad.radiantOrb));
                                            }
                                            else
                                                player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.radiantOrb));
                                            p.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                            return true;
                                        case "saving-grace":
                                            if(player.getInventory().firstEmpty()!=-1) {
                                                player.getInventory().addItem(utils.makeUnique(ConfigLoad.savingGraceOrb));
                                            }
                                            else
                                                player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.savingGraceOrb));
                                            p.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                            return true;
                                        case "tree-spawner":
                                            if(player.getInventory().firstEmpty()!=-1) {
                                                player.getInventory().addItem(utils.makeUnique(ConfigLoad.treeSpawnerOrb));
                                            }
                                            else
                                                player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.treeSpawnerOrb));
                                            p.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                            return true;
                                        default:
                                            p.sendMessage(utils.chat("&cInvalid orb type."));
                                            p.sendMessage(utils.chat("&fList of orb types: wheat, carrots, potatoes, beetroot, nether-wart, sugar-cane, fishing, flower, rainbow, radiant, saving-grace, tree-spawner"));
                                            return true;
                                    }
                                }
                                else
                                    p.sendMessage(utils.chat("&fUse: &3/handyorbs give <player> <orbType>"));
                            }
                            else
                                p.sendMessage(utils.chat(plugin.getConfig().getString("can-not-find-player-message")));
                        }
                        else {
                            p.sendMessage(utils.chat("&fUse: &3/handyorbs give <player> <orbType>"));
                        }
                    }
                    else {
                        p.sendMessage(utils.chat(plugin.getConfig().getString("no-permission-message")));
                        return false;
                    }
                }
                else if(args[0].equalsIgnoreCase("help")){
                    p.sendMessage(utils.chat("&3/handyorbs"+plugin.getConfig().getString("handyorbs-description")));
                    if(plugin.getConfig().getString("open-other-orbs-menu-permission").equalsIgnoreCase("none") || p.hasPermission(plugin.getConfig().getString("open-other-orbs-menu-permission")))
                        p.sendMessage(utils.chat("&3/handyorbs"+plugin.getConfig().getString("handyorbs-player-description")));
                    if(p.hasPermission(plugin.getConfig().getString("open-get-menu-permission")))
                        p.sendMessage(utils.chat("&3/handyorbs get"+plugin.getConfig().getString("handyorbs-get-description")));
                    if(p.hasPermission(plugin.getConfig().getString("give-permission")))
                        p.sendMessage(utils.chat("&3/handyorbs give <player> <orbType>"+plugin.getConfig().getString("handyorbs-give-description")));
                    if(p.hasPermission("handyorbs.reload"))
                        p.sendMessage(utils.chat("&3/handyorbs reload"));
                }
                else if(args[0].equalsIgnoreCase("reload")){
                    if(p.hasPermission("handyorbs.reload")) {
                        p.sendMessage(utils.chat("&aPlugin is reloading..."));
                        plugin.reloadConfig();
                        ConfigLoad.isReloading = true;
                        plugin.getServer().getPluginManager().disablePlugin(plugin);
                        plugin.getServer().getPluginManager().enablePlugin(plugin);
                        ConfigLoad.isReloading = false;
                        p.sendMessage(utils.chat("&aPlugin reloaded!"));
                    }
                    else {
                        p.sendMessage(utils.chat(plugin.getConfig().getString("no-permission-message")));
                        return false;
                    }
                }
                else{
                    if(plugin.getConfig().getString("open-other-orbs-menu-permission").equalsIgnoreCase("none") || p.hasPermission(plugin.getConfig().getString("open-other-orbs-menu-permission")))
                    {
                        if (args[0].equalsIgnoreCase("Server")) {
                            PlayerData.ownOrbsMenu(p, "Server");
                        } else {
                            String pData = Bukkit.getServer().getOfflinePlayer(args[0]).getUniqueId().toString();
                            if (PlayerData.totalOrbs(pData) != 0)
                                PlayerData.ownOrbsMenu(p, pData);
                            else
                                p.sendMessage(utils.chat(plugin.getConfig().getString("player-has-no-orbs")));
                        }
                    }
                    else {
                        p.sendMessage(utils.chat(plugin.getConfig().getString("no-permission-message")));
                        return false;
                    }
                }

        }
        else {
            if(args.length==0){
                sender.sendMessage("From console you can only execute:");
                sender.sendMessage("/handyorbs give <player> <orbType>");
                sender.sendMessage("/handyorbs reload");
            }
            else if(args[0].equals("reload")){
                sender.sendMessage(utils.chat("&aPlugin is reloading..."));
                plugin.reloadConfig();
                ConfigLoad.isReloading = true;
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                plugin.getServer().getPluginManager().enablePlugin(plugin);
                ConfigLoad.isReloading = false;
                sender.sendMessage(utils.chat("&aPlugin reloaded!"));
            }
            else if (args[0].equalsIgnoreCase("give")) {
                if (args.length >= 2) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player != null) {
                        if (args.length >= 3) {
                            switch (args[2]) {
                                case "wheat":
                                    if (player.getInventory().firstEmpty() != -1) {
                                        player.getInventory().addItem(utils.makeUnique(ConfigLoad.farmingWheatOrb));
                                    } else
                                        player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.farmingWheatOrb));
                                    sender.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                    return true;
                                case "carrots":
                                    if (player.getInventory().firstEmpty() != -1) {
                                        player.getInventory().addItem(utils.makeUnique(ConfigLoad.farmingCarrotsOrb));
                                    } else
                                        player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.farmingCarrotsOrb));
                                    sender.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                    return true;
                                case "potatoes":
                                    if (player.getInventory().firstEmpty() != -1) {
                                        player.getInventory().addItem(utils.makeUnique(ConfigLoad.farmingPotatoesOrb));
                                    } else
                                        player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.farmingPotatoesOrb));
                                    sender.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                    return true;
                                case "beetroot":
                                    if(!Bukkit.getVersion().contains("1.8")) {
                                        if (player.getInventory().firstEmpty() != -1) {
                                            player.getInventory().addItem(utils.makeUnique(ConfigLoad.farmingBeetrootOrb));
                                        } else
                                            player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.farmingBeetrootOrb));
                                        sender.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                    }
                                    return true;
                                case "nether-wart":
                                    if (player.getInventory().firstEmpty() != -1) {
                                        player.getInventory().addItem(utils.makeUnique(ConfigLoad.netherWartOrb));
                                    } else
                                        player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.netherWartOrb));
                                    sender.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                    return true;
                                case "sugar-cane":
                                    if (player.getInventory().firstEmpty() != -1) {
                                        player.getInventory().addItem(utils.makeUnique(ConfigLoad.sugarCaneOrb));
                                    } else
                                        player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.sugarCaneOrb));
                                    sender.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                    return true;
                                case "fishing":
                                    if (player.getInventory().firstEmpty() != -1) {
                                        player.getInventory().addItem(utils.makeUnique(ConfigLoad.fishingOrb));
                                    } else
                                        player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.fishingOrb));
                                    sender.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                    return true;
                                case "flower":
                                    if (player.getInventory().firstEmpty() != -1) {
                                        player.getInventory().addItem(utils.makeUnique(ConfigLoad.flowerOrb));
                                    } else
                                        player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.flowerOrb));
                                    sender.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                    return true;
                                case "rainbow":
                                    if (player.getInventory().firstEmpty() != -1) {
                                        player.getInventory().addItem(utils.makeUnique(ConfigLoad.rainbowOrb));
                                    } else
                                        player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.rainbowOrb));
                                    sender.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                    return true;
                                case "radiant":
                                    if (player.getInventory().firstEmpty() != -1) {
                                        player.getInventory().addItem(utils.makeUnique(ConfigLoad.radiantOrb));
                                    } else
                                        player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.radiantOrb));
                                    sender.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                    return true;
                                case "saving-grace":
                                    if (player.getInventory().firstEmpty() != -1) {
                                        player.getInventory().addItem(utils.makeUnique(ConfigLoad.savingGraceOrb));
                                    } else
                                        player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.savingGraceOrb));
                                    sender.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                    return true;
                                case "tree-spawner":
                                    if (player.getInventory().firstEmpty() != -1) {
                                        player.getInventory().addItem(utils.makeUnique(ConfigLoad.treeSpawnerOrb));
                                    } else
                                        player.getWorld().dropItem(player.getLocation(), utils.makeUnique(ConfigLoad.treeSpawnerOrb));
                                    sender.sendMessage(utils.chat(plugin.getConfig().getString("given-orb-message")).replace("%player%", player.getName()));
                                    return true;
                                default:
                                    sender.sendMessage(utils.chat("&cInvalid orb type."));
                                    sender.sendMessage(utils.chat("&fList of orb types: wheat, carrots, potatoes, beetroot, nether-wart, sugar-cane, fishing, flower, rainbow, radiant, saving-grace, tree-spawner"));
                                    return true;
                            }
                        } else
                            sender.sendMessage(utils.chat("&fUse: &3/handyorbs give <player> <orbType>"));
                    } else
                        sender.sendMessage(utils.chat(plugin.getConfig().getString("can-not-find-player-message")));
                }
            }
            else{
                sender.sendMessage("From console you can only execute:");
                sender.sendMessage("/handyorbs give <player> <orbType>");
                sender.sendMessage("/handyorbs reload");
            }
        }
        return false;
    }

}
