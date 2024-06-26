package thirtyvirus.uber.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import thirtyvirus.uber.UberItem;
import thirtyvirus.uber.UberItems;
import thirtyvirus.uber.UberMaterial;
import thirtyvirus.uber.helpers.Utilities;

public class UberCommand implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Verify that the user has proper permissions
        if (!sender.hasPermission("uber.user")) {
            Utilities.warnPlayer(sender, UberItems.getPhrase("no-permissions-message"));
            return true;
        }

        // Default to help command if no arguments are typed
        if (args.length == 0) {
            help(sender);
            return true;
        }

        //try {
            switch (args[0].toLowerCase()) {

                // Standard plugin commands
                case "help":
                    help(sender);
                    break;
                case "info":
                    info(sender);
                    break;

                // Plugin specific user commands
                case "identify":
                    identify(sender, args);
                    break;
                case "list":
                    list(sender);
                    break;

                // Staff Commands
                case "give":
                    if (sender.hasPermission("uber.admin")) give(sender, args, false);
                    else Utilities.warnPlayer(sender, UberItems.getPhrase("no-permissions-message"));
                    break;
                case "givematerial":
                    if (sender.hasPermission("uber.admin")) give(sender, args, true);
                    else Utilities.warnPlayer(sender, UberItems.getPhrase("no-permissions-message"));
                    break;
                case "updatelore":
                    if (sender.hasPermission("uber.admin")) updateLore(sender);
                    else Utilities.warnPlayer(sender, UberItems.getPhrase("no-permissions-message"));
                    break;
                case "setmana":
                    if (sender.hasPermission("uber.admin")) setMana(sender, args);
                    else Utilities.warnPlayer(sender, UberItems.getPhrase("no-permissions-message"));
                    break;
                case "setmaxmana":
                    if (sender.hasPermission("uber.admin")) setMaxMana(sender, args);
                    else Utilities.warnPlayer(sender, UberItems.getPhrase("no-permissions-message"));
                    break;
                case "reload":
                    if (sender.hasPermission("uber.admin")) reload(sender);
                    else Utilities.warnPlayer(sender, UberItems.getPhrase("no-permissions-message"));
                    break;

                default:
                    Utilities.warnPlayer(sender, UberItems.getPhrase("not-a-command-message"));
                    help(sender);
                    break;
            }

        //} catch(Exception e) {
       //     Utilities.warnPlayer(sender, UberItems.getPhrase("formatting-error-message"));
        //}

        return true;
    }

    // updateLore command
    private void updateLore(CommandSender sender) {
        // verify that the command is executed by a player
        if (!(sender instanceof Player)) { Utilities.warnPlayer(sender, UberItems.getPhrase("no-console-message")); return; }
        Player player = (Player) sender;

        // verify that the item is in fact an UberItem
        UberItem uber = Utilities.getUber(player.getInventory().getItemInMainHand());
        if (uber == null) { Utilities.warnPlayer(sender, UberItems.getPhrase("not-uberitem")); return; }

        // update the lore
        uber.updateLore(player.getInventory().getItemInMainHand());
        Utilities.informPlayer(player, UberItems.getPhrase("updated-lore-message"));
    }

    // setMana command
    private void setMana(CommandSender sender, String[] args) {

        // modify a player's mana
        if (args.length == 3) {
            Player player = Bukkit.getPlayer(args[1]);
            double mana = Double.parseDouble(args[2]);
            if (mana >= 0) Utilities.mana.put(player, Math.min(mana, Utilities.maxMana.get(player)));
        }
        // modify your own mana (Player only)
        else {
            // verify that the command is executed by a player
            if (!(sender instanceof Player)) { Utilities.warnPlayer(sender, UberItems.getPhrase("no-console-message")); return; }
            Player player = (Player) sender;

            if (args.length == 2) {
                double mana = Double.parseDouble(args[1]);
                if (mana >= 0) Utilities.mana.put(player, Math.min(mana, Utilities.maxMana.get(player)));
            }
        }

    }

    // setMaxMana command
    private void setMaxMana(CommandSender sender, String[] args) {

        // modify a player's max mana
        if (args.length == 3) {
            Player player = Bukkit.getPlayer(args[1]);
            double maxMana = Double.parseDouble(args[2]);
            if (maxMana >= 0) Utilities.maxMana.put(player, maxMana);
            if (Utilities.mana.get(player) > maxMana) Utilities.mana.put(player, maxMana);
        }
        else {
            // verify that the command is executed by a player
            if (!(sender instanceof Player)) { Utilities.warnPlayer(sender, UberItems.getPhrase("no-console-message")); return; }
            Player player = (Player) sender;

            if (args.length == 2) {
                double maxMana = Double.parseDouble(args[1]);
                if (maxMana >= 0) Utilities.maxMana.put(player, maxMana);
                if (Utilities.mana.get(player) > maxMana) Utilities.mana.put(player, maxMana);
            }
        }

    }

    // give command (also works for "/giveMaterial"). Variations:
    // "/uber give itemname"
    // "/uber give itemname amount"
    // "/uber give playername itemname"
    // "/uber give playername itemname amount"
    private void give(CommandSender sender, String[] args, boolean material) {
        // Determine the action based on argument length and content
        Player targetPlayer = null;
        String itemName;
        int amount = 1; // Default amount

        // Checking the number of arguments to handle different cases
        switch (args.length) {
            case 2:
                // Could be either "/uber give itemname" or "/uber give playername itemname" (implied)

                // Attempt to parse as item name directly for the sender
                itemName = args[1];
                targetPlayer = sender instanceof Player ? (Player) sender : null;
                if (targetPlayer == null) {
                    Utilities.warnPlayer(sender, "You cannot give UberItems to the console! Specify a player name to give the item to");
                    return;
                }
                break;
            case 3:
                // Could be "/uber give itemname amount" or "/uber give playername itemname"
                try {
                    // Attempt to determine if third argument is a number (amount)
                    amount = Integer.parseInt(args[2]);
                    itemName = args[1];
                    targetPlayer = sender instanceof Player ? (Player) sender : null;
                } catch (NumberFormatException e) {
                    // Third argument is not a number, treat as playername itemname
                    targetPlayer = Bukkit.getPlayer(args[1]);
                    itemName = args[2];
                    if (targetPlayer == null) {
                        Utilities.warnPlayer(sender, "Player not found.");
                        return;
                    }
                }
                break;
            case 4:
                // Can only be "/uber give playername itemname amount"
                targetPlayer = Bukkit.getPlayer(args[1]);
                if (targetPlayer == null) {
                    Utilities.warnPlayer(sender, "Player not found.");
                    return;
                }
                itemName = args[2];
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    Utilities.warnPlayer(sender, "Invalid amount. Please specify a valid number.");
                    return;
                }
                break;
            default:
                Utilities.warnPlayer(sender, "Incorrect command usage.");
                return;
        }

        // Create the ItemStack from name (either an UberItem or UberMaterial)
        ItemStack uber;
        if (!material) uber = UberItems.getItem(itemName).makeItem(amount);
        else uber = UberItems.getMaterial(itemName).makeItem(amount);

        // Verify that the item is an UberItem
        if (uber == null || itemName.equals("null") || UberItems.getItem("null").compare(uber) || UberItems.getMaterial("null").compare(uber)) {
            Utilities.warnPlayer(sender, UberItems.getPhrase("not-uberitem"));
            return;
        }

        // Give the item to the target player
        Utilities.givePlayerItemSafely(targetPlayer, uber);

        String message = UberItems.prefix + ChatColor.GRAY + "Given " + uber.getItemMeta().getDisplayName() + ChatColor.GRAY + " x" + amount;
        if (sender.equals(targetPlayer)) Utilities.informPlayer(sender, message);
        else {
            Utilities.informPlayer(sender, message + " to player " + targetPlayer.getName());
            Utilities.informPlayer(targetPlayer, message);
        }
    }

    // identify Command
    private void identify(CommandSender sender, String[] args) {

        // verify that the command is executed by a player
        if (!(sender instanceof Player)) { Utilities.warnPlayer(sender, UberItems.getPhrase("no-console-message")); return; }
        Player player = (Player) sender;

        // identify UberItem(s) held by the player (main or offhand)
        if (args.length == 1) {
            UberItem mainHand = Utilities.getUber(player.getInventory().getItemInMainHand());
            UberItem offHand = Utilities.getUber(player.getInventory().getItemInOffHand());

            if (mainHand != null && offHand != null) {
                player.sendMessage(UberItems.prefix + ChatColor.YELLOW + "Main Hand - " + mainHand.getName());
                player.sendMessage(UberItems.prefix + ChatColor.YELLOW + "Off  Hand - " + offHand.getName());
                return;
            }
            else if (mainHand != null) {
                player.sendMessage(UberItems.prefix + ChatColor.YELLOW + mainHand.getName());
                return;
            }
            else if (offHand != null) {
                player.sendMessage(UberItems.prefix + ChatColor.YELLOW + offHand.getName());
                return;
            }
            else {
                Utilities.warnPlayer(sender, UberItems.getPhrase("not-uberitem"));
                return;
            }
        }

        // identify an UberItem by name

        // get the item from either numerical ID or name
        UberItem item = UberItems.getItem(args[1]);

        // verify that the item is in fact an UberItem
        if (item == null) { Utilities.warnPlayer(sender, UberItems.getPhrase("not-uberitem")); return; }

        // tell player what
        player.sendMessage(UberItems.prefix + ChatColor.YELLOW + item.getName());
    }

    // list Command
    private void list(CommandSender sender) {
        sender.sendMessage(UberItems.prefix + ChatColor.GRAY + "Listing UberItems:");

        for (UberItem item : UberItems.getItems()) {
            if (!Utilities.enforcePermissions(sender, item, false))
                sender.sendMessage(ChatColor.GOLD + "" + item.getRarity().getColor() + item.getName());
        }
    }

    // info command
    private void info(CommandSender sender) {
        sender.sendMessage(UberItems.prefix + ChatColor.GRAY + "UberItems Plugin Info");
        sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.GREEN + "Version " + UberItems.getInstance().getVersion() + " - By ThirtyVirus");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.GREEN + "~Add powerful and fun items to your Minecraft Server!");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "You" + ChatColor.WHITE + ChatColor.BOLD + "Tube" + ChatColor.GREEN + " - https://youtube.com/ThirtyVirus");
        sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.RESET + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Twitter" + ChatColor.GREEN + " - https://twitter.com/ThirtyVirus");
        sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.RESET + ChatColor.GOLD + "" + ChatColor.BOLD + "SpigotMC" + ChatColor.GREEN + " - https://www.spigotmc.org/members/ThirtyVirus.179587");
        sender.sendMessage(ChatColor.DARK_PURPLE + "------------------------------");
    }

    // help command
    private void help(CommandSender sender) {
        sender.sendMessage(UberItems.prefix + ChatColor.GRAY + "Commands");
        sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.GRAY + "/uber help");
        sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.GRAY + "/uber info");
        sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.GRAY + "/uber identify");
        sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.GRAY + "/uber list");
        if (sender.hasPermission("uber.admin")) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.GRAY + "/uber give");
            sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.GRAY + "/uber giveMaterial");
            sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.GRAY + "/uber updateLore");
            sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.GRAY + "/uber setMana");
            sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.GRAY + "/uber setMaxMana");
            sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.GRAY + "/uber reload");
        }
        sender.sendMessage(ChatColor.DARK_PURPLE + "------------------------------");
    }

    // reload the config and language files in real time
    private void reload(CommandSender sender) {
        UberItems.reload();
        Utilities.informPlayer(sender, UberItems.getPhrase("reloaded-plugin-message"));
    }

}
