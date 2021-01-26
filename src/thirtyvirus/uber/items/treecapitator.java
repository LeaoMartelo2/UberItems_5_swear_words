package thirtyvirus.uber.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import thirtyvirus.uber.UberItem;
import thirtyvirus.uber.UberItems;
import thirtyvirus.uber.helpers.UberAbility;
import thirtyvirus.uber.helpers.UberRarity;
import thirtyvirus.uber.helpers.Utilities;

// a template class that can be copy - pasted and renamed when making new Uber Items
public class treecapitator extends UberItem{

    public treecapitator(int id, UberRarity rarity, String name, Material material, boolean stackable, boolean oneTimeUse, boolean hasActiveEffect, List<UberAbility> abilities) {
        super(id, rarity, name, material, stackable, oneTimeUse, hasActiveEffect, abilities);
    }
    public void onItemStackCreate(ItemStack item) { }
    public void getSpecificLorePrefix(List<String> lore, ItemStack item) {
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "A forceful Gold Axe which can");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "break a large amount of logs in");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "a single hit!");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + "2s");
    }
    public void getSpecificLoreSuffix(List<String> lore, ItemStack item) { }

    public void leftClickAirAction(Player player, ItemStack item) { }
    public void leftClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item) { }
    public void rightClickAirAction(Player player, ItemStack item) { }
    public void rightClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item) { }
    public void shiftLeftClickAirAction(Player player, ItemStack item) { }
    public void shiftLeftClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item) { }
    public void shiftRightClickAirAction(Player player, ItemStack item) { }
    public void shiftRightClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item) { }
    public void middleClickAction(Player player, ItemStack item) { }
    public void hitEntityAction(Player player, EntityDamageByEntityEvent event, Entity target, ItemStack item) { }
    public void breakBlockAction(Player player, BlockBreakEvent event, Block block, ItemStack item) {

        // enforce the 2 second cooldown of the treecap "ability"
        if (!Utilities.enforceCooldown(player, "treecap", 2, item, false)) return;

        if (block.getType().name().contains("LOG") || block.getType().name().contains("LEAVES"))
            doBlockEater(player, block, 35);

        // repair axe each time it is used
        Utilities.repairItem(item);
    }
    public void clickedInInventoryAction(Player player, InventoryClickEvent event) { }
    public void activeEffect(Player player, ItemStack item) { }

    public void doBlockEater(Player player, Block startingBlock, int amount) {
        if (startingBlock.getType() == Material.AIR) return;
        Material targetMaterial = startingBlock.getType();

        ArrayList<Block> blocksToCheck = new ArrayList<>();
        blocksToCheck.add(startingBlock);

        for (int i = 0; i <= amount; i++) {
            Utilities.scheduleTask(() -> {
                ArrayList<Block> preClonedList = new ArrayList<>(blocksToCheck);
                for (Block block : preClonedList) {
                    blocksToCheck.remove(block);
                    Block upperBlock = block.getRelative(BlockFace.UP);
                    Block lowerBlock = block.getRelative(BlockFace.DOWN);
                    Block northBlock = block.getRelative(BlockFace.NORTH);
                    Block eastBlock = block.getRelative(BlockFace.EAST);
                    Block southBlock = block.getRelative(BlockFace.SOUTH);
                    Block westBlock = block.getRelative(BlockFace.WEST);
                    for (Block nearbyBlock : new ArrayList<Block>(Arrays.asList(upperBlock, lowerBlock, northBlock, eastBlock, southBlock, westBlock))) {
                        if (nearbyBlock.getType() == targetMaterial) {
                            if (nearbyBlock.getType().name().contains("LOG")) {
                                player.getInventory().addItem(new ItemStack(targetMaterial, 1));
                                nearbyBlock.getWorld().playSound(nearbyBlock.getLocation(), Sound.BLOCK_WOOD_BREAK, 0.3F, 2F);
                            }
                            if (nearbyBlock.getType().name().contains("LEAVES")) {
                                Random rand = new Random();
                                int n = rand.nextInt(200);
                                if (n > 189) player.getInventory().addItem(new ItemStack(Material.OAK_SAPLING, 1));
                                if (n < 6) player.getInventory().addItem(new ItemStack(Material.APPLE, 1));
                                nearbyBlock.getWorld().playSound(nearbyBlock.getLocation(), Sound.BLOCK_GRASS_BREAK, 0.3F, 2F);
                            }
                            nearbyBlock.setType(Material.AIR);

                            blocksToCheck.add(nearbyBlock);
                        }
                    }
                }
            }, i*2);
        }
    }
}