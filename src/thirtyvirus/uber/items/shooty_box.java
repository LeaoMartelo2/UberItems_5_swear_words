package thirtyvirus.uber.items;

import java.util.*;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.util.Vector;

import thirtyvirus.uber.UberItem;
import thirtyvirus.uber.helpers.*;

public class shooty_box extends UberItem implements Listener {

	public shooty_box(Material material, String name, UberRarity rarity, boolean stackable, boolean oneTimeUse, boolean hasActiveEffect, List<UberAbility> abilities, UberCraftingRecipe craftingRecipe) {
		super(material, name, rarity, stackable, oneTimeUse, hasActiveEffect, abilities, craftingRecipe);
	}
	public void onItemStackCreate(ItemStack item) { }
	public void getSpecificLorePrefix(List<String> lore, ItemStack item) { }
	public void getSpecificLoreSuffix(List<String> lore, ItemStack item) { }

	public boolean leftClickAirAction(Player player, ItemStack item) { return false; }
	public boolean leftClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item) { return false; }

	// shoot item from box
	public boolean rightClickAirAction(Player player, ItemStack item) {
		
		// get all items inside shooty box
		ItemStack[] rawItems = Utilities.getCompactInventory(item);
		ArrayList<ItemStack> items = new ArrayList<>();
		for (ItemStack i : rawItems) if (i != null) items.add(i);
		
		// play "empty" sound when no items in dispenser
		if (items.isEmpty()) { player.getWorld().playEffect(player.getLocation(), Effect.CLICK1, 1); return false; }
		
		// pick random Item
		Random ran = new Random();
		ItemStack actionItem = items.get(ran.nextInt(items.size()));
		
		// perform dispenser action
		shootItem(player, actionItem);

		// delete ActionItem stack if last item used
		if (actionItem.getAmount() == 0) items.remove(actionItem);
		
		// save inventory update to item lore
		ItemStack[] finalItems = new ItemStack[items.size()];
		for (int counter = 0; counter < items.size(); counter++) finalItems[counter] = items.get(counter);
		Utilities.saveCompactInventory(item, finalItems);

		return true;
	}
	public boolean rightClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item) {
		return rightClickAirAction(player, item);
	}

	public boolean shiftLeftClickAirAction(Player player, ItemStack item) { player.openInventory(createShootyBoxAmmoGuide()); return false; }
	public boolean shiftLeftClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item) { return shiftLeftClickAirAction(player, item); }

	// open the box's inventory
	public boolean shiftRightClickAirAction(Player player, ItemStack item) {
		Inventory inventory = Bukkit.createInventory(player, InventoryType.DISPENSER, "Shooty Box");
		
		ItemStack[] items = Utilities.getCompactInventory(item);
		if (items != null) { for (ItemStack i : items) { if (i != null) { inventory.addItem(i); } } }
		player.openInventory(inventory);
		
		player.playSound(player.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1, 1);
		return false;
	}
	public boolean shiftRightClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item) {
		return shiftRightClickAirAction(player, item);
	}

	public boolean middleClickAction(Player player, ItemStack item) { return false; }
	public boolean hitEntityAction(Player player, EntityDamageByEntityEvent event, Entity target, ItemStack item) { return false; }
	public boolean breakBlockAction(Player player, BlockBreakEvent event, Block block, ItemStack item) { return false; }
	public boolean clickedInInventoryAction(Player player, InventoryClickEvent event, ItemStack item, ItemStack addition) { return false; }
	public boolean activeEffect(Player player, ItemStack item) { return false; }

	// create the UberItems ShootyBox ammo guide (Shift + Left Click while holding one)
	private Inventory createShootyBoxAmmoGuide() {
		// prep inventory elements
		Inventory i = Bukkit.createInventory(null, 54, "UberItems ShootyBox Ammo Guide");
		for (int index : MenuUtils.CRAFTING_GUIDE_MENU_EXCEPTIONS) i.setItem(index, MenuUtils.EMPTY_SLOT_ITEM);
		i.setItem(48, MenuUtils.EMPTY_SLOT_ITEM);
		i.setItem(49, MenuUtils.BACK_BUTTON);
		i.setItem(50, MenuUtils.EMPTY_SLOT_ITEM);

		List<ItemStack> guideItems = Arrays.asList(
				mgi(Material.ARROW, "Shoots the arrow at high speeds, without the need to pull a bowstring back!"),
				mgi(Material.FLINT, "Shoots a precise projectile long distances, can break blocks! 5x flint yield when used on gravel."),
				mgi(Material.SAND, "Throw sand in front of you, damaging enemies."),
				mgi(Material.GRAVEL, "Throw gravel in front of you, hurts enemies pretty badly."),
				mgi(Material.GLASS, "Launches shards of sharp glass at your enemies, yikes that's gotta hurt."),
				mgi(Material.GUNPOWDER, "Doesn't actually shoot much other than a lot of hot air, could be useful to knock enemies back."),
				mgi(Material.FIREWORK_ROCKET, "Shoots nothing, but JEEZ that recoil is insane."),
				mgi(Material.FIRE_CHARGE, "Shoots a small fireball, light blocks and enemies on fire!"),
				mgi(Material.TNT, "Throws a Primed TNT. Watch out, the blast is dangerous and there is some crazy recoil."),
				mgi(Material.ENDER_PEARL, "Imagine if a major league baseball player could throw a pearl... ya."),
				mgi(Material.EGG, "Throws an egg at high speed. What did you think was gonna happen?"),
				mgi(Material.SPLASH_POTION, "Throw potions a half mile away."),
				mgi(Material.WATER_BUCKET, "chucks water a mile away, because why not."),
				mgi(Material.LAVA_BUCKET, "a griefer's best friend."),
				mgi(Material.CREEPER_SPAWN_EGG, "Wait, this thing can shoot MOBS???"),
				mgi(Material.DIRT, "So ya, you can shoot blocks of any type. Neat!")
		);

		for (int counter = 0; counter < MenuUtils.CRAFTING_GUIDE_ITEM_SLOTS.size(); counter++) {
			if (counter >= guideItems.size()) break;
			i.setItem(MenuUtils.CRAFTING_GUIDE_ITEM_SLOTS.get(counter), guideItems.get(counter));
		}

		return i;
	}

	// make guide item (MGI)
	private ItemStack mgi(Material material, String description) {
		ItemStack item = new ItemStack(material);
		Utilities.loreItem(item, Utilities.stringToLore(description, 25, ChatColor.GRAY));
		return item;
	}

	// perform dispenser action
	private void shootItem(Player player, ItemStack item) {
		float recoil = 0.5F;

		switch (item.getType()) {
			case ARROW:
				player.launchProjectile(Arrow.class);
				player.getWorld().playEffect(player.getLocation(), Effect.BOW_FIRE, 1);
				recoil = 0;
				break;
			case TIPPED_ARROW:
				Arrow arrow = player.launchProjectile(Arrow.class);
				PotionMeta meta = (PotionMeta) item.getItemMeta();
				arrow.setBasePotionData(meta.getBasePotionData());
				break;
			case SPECTRAL_ARROW:
				SpectralArrow spec = player.launchProjectile(SpectralArrow.class);
				break;
			case TNT:
				Entity tnt = player.getWorld().spawn(player.getEyeLocation(), TNTPrimed.class);
				((TNTPrimed)tnt).setFuseTicks(30);
				((TNTPrimed)tnt).setVelocity(player.getEyeLocation().add(0, 1, 0).getDirection().multiply(2.0));
				player.getWorld().playEffect(player.getLocation(), Effect.BOW_FIRE, 1);
				recoil = 2;
				break;
			case EGG:
				Egg thrown = player.launchProjectile(Egg.class);
				thrown.setVelocity(player.getEyeLocation().getDirection().multiply(1.5));
				player.getWorld().playEffect(player.getLocation(), Effect.BOW_FIRE, 1);
				recoil = 0;
				break;
			case ENDER_PEARL:
				EnderPearl pearl = player.launchProjectile(EnderPearl.class);
				pearl.setVelocity(player.getEyeLocation().getDirection().multiply(3.0));
				player.getWorld().playEffect(player.getLocation(), Effect.BOW_FIRE, 1);
				recoil = 0;
				break;
			case SPLASH_POTION:
				ThrownPotion potion = player.launchProjectile(ThrownPotion.class);
				potion.setVelocity(player.getEyeLocation().getDirection().multiply(2.0));
				potion.setItem(item);

				player.getWorld().playEffect(player.getLocation(), Effect.BOW_FIRE, 1);
				break;
			case FIRE_CHARGE:
				Fireball fireball = player.launchProjectile(SmallFireball.class);
				fireball.setVelocity(player.getEyeLocation().getDirection().multiply(2.0));
				player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 1);
				break;
			// TODO add particles to projectile
			case WATER_BUCKET:
				launchFallingBlock(player, Material.WATER, 2.0F, Effect.BOW_FIRE);
				break;
			// TODO add particles to projectile
			case LAVA_BUCKET:
				//FallingBlock proj = launchFallingBlock(player, Material.LAVA, 2.0F, Effect.BOW_FIRE);
				launchFallingBlock(player, Material.LAVA, 2.0F, Effect.BOW_FIRE);
				break;
			case FIREWORK_ROCKET:
				recoil = 5;
				player.getWorld().playEffect(player.getLocation(), Effect.FIREWORK_SHOOT, 1);
				break;
			case SAND:
				spritzAttack(player, 7, 5);
				player.getWorld().playEffect(player.getLocation(), Effect.EXTINGUISH, 1);
				recoil = 0.6F;
				break;
			case GRAVEL:
				spritzAttack(player, 10, 6);
				player.getWorld().playEffect(player.getLocation(), Effect.EXTINGUISH, 1);
				recoil = 0.6F;
				break;
			case GLASS:
				spritzAttack(player, 15, 8);
				player.getWorld().playEffect(player.getLocation(), Effect.POTION_BREAK, 1);
				recoil = 0.6F;
				break;
			case GUNPOWDER:
				knockbackAttack(player, 8);
				player.getWorld().playSound(player.getLocation().add(0,1,0), Sound.ENTITY_GENERIC_EXPLODE, 1,1);
				recoil = 2.5F;
				break;
			case FLINT:
				Block target = Utilities.getBlockLookingAt(player);
				if (target.getType() == Material.GRAVEL) {
					player.getWorld().dropItemNaturally(target.getLocation(), new ItemStack(Material.FLINT, 5));
					target.setType(Material.AIR);
				}
				else if (target.getType().getBlastResistance() <= 1200 && !target.isLiquid()) target.breakNaturally();
				recoil = 0.2F;
				target.getWorld().playEffect(target.getLocation(), Effect.WITHER_BREAK_BLOCK, 1);
				break;
			case SALMON_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.SALMON, 3.0F, Effect.BLAZE_SHOOT); break;
			case SHEEP_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.SHEEP, 3.0F, Effect.BLAZE_SHOOT); break;
			case SHULKER_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.SHULKER, 3.0F, Effect.BLAZE_SHOOT); break;
			case SILVERFISH_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.SILVERFISH, 3.0F, Effect.BLAZE_SHOOT); break;
			case SKELETON_HORSE_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.SKELETON_HORSE, 3.0F, Effect.BLAZE_SHOOT); break;
			case SKELETON_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.SKELETON, 3.0F, Effect.BLAZE_SHOOT); break;
			case SLIME_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.SLIME, 3.0F, Effect.BLAZE_SHOOT); break;
			case SPIDER_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.SPIDER, 3.0F, Effect.BLAZE_SHOOT); break;
			case SQUID_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.SQUID, 3.0F, Effect.BLAZE_SHOOT); break;
			case STRAY_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.STRAY, 3.0F, Effect.BLAZE_SHOOT); break;
			case BAT_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.BAT, 3.0F, Effect.BLAZE_SHOOT); break;
			case BLAZE_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.BLAZE, 3.0F, Effect.BLAZE_SHOOT); break;
			case CAVE_SPIDER_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.CAVE_SPIDER, 3.0F, Effect.BLAZE_SHOOT); break;
			case CHICKEN_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.CHICKEN, 3.0F, Effect.BLAZE_SHOOT); break;
			case COD_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.COD, 3.0F, Effect.BLAZE_SHOOT); break;
			case COW_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.COW, 3.0F, Effect.BLAZE_SHOOT); break;
			case CREEPER_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.CREEPER, 3.0F, Effect.BLAZE_SHOOT); break;
			case DOLPHIN_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.DOLPHIN, 3.0F, Effect.BLAZE_SHOOT); break;
			case DONKEY_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.DONKEY, 3.0F, Effect.BLAZE_SHOOT); break;
			case DROWNED_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.DROWNED, 3.0F, Effect.BLAZE_SHOOT); break;
			case ELDER_GUARDIAN_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.ELDER_GUARDIAN, 3.0F, Effect.BLAZE_SHOOT); break;
			case ENDERMAN_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.ENDERMAN, 3.0F, Effect.BLAZE_SHOOT); break;
			case ENDERMITE_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.ENDERMITE, 3.0F, Effect.BLAZE_SHOOT); break;
			case EVOKER_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.EVOKER, 3.0F, Effect.BLAZE_SHOOT); break;
			case GHAST_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.GHAST, 3.0F, Effect.BLAZE_SHOOT); break;
			case GUARDIAN_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.GUARDIAN, 3.0F, Effect.BLAZE_SHOOT); break;
			case HORSE_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.HORSE, 3.0F, Effect.BLAZE_SHOOT); break;
			case HUSK_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.HUSK, 3.0F, Effect.BLAZE_SHOOT); break;
			case LLAMA_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.LLAMA, 3.0F, Effect.BLAZE_SHOOT); break;
			case MAGMA_CUBE_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.MAGMA_CUBE, 3.0F, Effect.BLAZE_SHOOT); break;
			case MOOSHROOM_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.MUSHROOM_COW, 3.0F, Effect.BLAZE_SHOOT); break;
			case MULE_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.MULE, 3.0F, Effect.BLAZE_SHOOT); break;
			case OCELOT_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.OCELOT, 3.0F, Effect.BLAZE_SHOOT); break;
			case PARROT_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.PARROT, 3.0F, Effect.BLAZE_SHOOT); break;
			case PHANTOM_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.PHANTOM, 3.0F, Effect.BLAZE_SHOOT); break;
			case PIG_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.PIG, 3.0F, Effect.BLAZE_SHOOT); break;
			case POLAR_BEAR_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.POLAR_BEAR, 3.0F, Effect.BLAZE_SHOOT); break;
			case PUFFERFISH_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.PUFFERFISH, 3.0F, Effect.BLAZE_SHOOT); break;
			case RABBIT_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.RABBIT, 3.0F, Effect.BLAZE_SHOOT); break;
			case TROPICAL_FISH_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.TROPICAL_FISH, 3.0F, Effect.BLAZE_SHOOT); break;
			case TURTLE_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.TURTLE, 3.0F, Effect.BLAZE_SHOOT); break;
			case VEX_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.VEX, 3.0F, Effect.BLAZE_SHOOT); break;
			case VILLAGER_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.VILLAGER, 3.0F, Effect.BLAZE_SHOOT); break;
			case VINDICATOR_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.VINDICATOR, 3.0F, Effect.BLAZE_SHOOT); break;
			case WITCH_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.WITCH, 3.0F, Effect.BLAZE_SHOOT); break;
			case WITHER_SKELETON_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.WITHER_SKELETON, 3.0F, Effect.BLAZE_SHOOT); break;
			case WOLF_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.WOLF, 3.0F, Effect.BLAZE_SHOOT); break;
			case ZOMBIE_HORSE_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.ZOMBIE_HORSE, 3.0F, Effect.BLAZE_SHOOT); break;
			case ZOMBIE_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.ZOMBIE, 3.0F, Effect.BLAZE_SHOOT); break;
			case ZOMBIE_VILLAGER_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.ZOMBIE_VILLAGER, 3.0F, Effect.BLAZE_SHOOT); break;
			//case STRIDER_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.STRIDER, 3.0F, Effect.BLAZE_SHOOT); break;
			//case BEE_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.BEE, 3.0F, Effect.BLAZE_SHOOT); break;
			case CAT_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.CAT, 3.0F, Effect.BLAZE_SHOOT); break;
			case FOX_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.FOX, 3.0F, Effect.BLAZE_SHOOT); break;
			//case HOGLIN_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.HOGLIN, 3.0F, Effect.BLAZE_SHOOT); break;
			case PANDA_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.PANDA, 3.0F, Effect.BLAZE_SHOOT); break;
			//case PIGLIN_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.PIGLIN, 3.0F, Effect.BLAZE_SHOOT); break;
			case PILLAGER_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.PILLAGER, 3.0F, Effect.BLAZE_SHOOT); break;
			case RAVAGER_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.RAVAGER, 3.0F, Effect.BLAZE_SHOOT); break;
			case TRADER_LLAMA_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.TRADER_LLAMA, 3.0F, Effect.BLAZE_SHOOT); break;
			case WANDERING_TRADER_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.WANDERING_TRADER, 3.0F, Effect.BLAZE_SHOOT); break;
			//case ZOGLIN_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.ZOGLIN, 3.0F, Effect.BLAZE_SHOOT); break;
			//case ZOMBIFIED_PIGLIN_SPAWN_EGG: launchMobFromSpawnEgg(player, EntityType.ZOMBIFIED_PIGLIN, 3.0F, Effect.BLAZE_SHOOT); break;

			default:
				// shoot blocks as falling sand
				if (item.getType().isBlock()) {
					FallingBlock bl = launchFallingBlock(player, item.getType(), 2.0F, Effect.BOW_FIRE);
					bl.setHurtEntities(true); 
				}
				else {
					// shoot item forward as default action
					ItemStack dropItem = item.clone(); dropItem.setAmount(1);
					Entity drop = (Entity) player.getWorld().dropItemNaturally(player.getEyeLocation(), dropItem);
					drop.setVelocity(player.getLocation().getDirection().multiply(1.5));
					recoil = 1;
					player.getWorld().playEffect(player.getLocation(), Effect.CLICK2, 1);
				}
				break;

			// IDEAS
			// particles for water and lava as it flew
			// glass, dyed glass ice, packed ice, blue ice shattering on impact and hurting a radius
			//

			// SYSTEMS
			// on block impact function
			// during block flight function (particles)
			// scattershot shift left click ability
		}

		// update inventory, apply recoil
		if (player.getGameMode() != GameMode.CREATIVE) {
			item.setAmount(item.getAmount() - 1);

			Vector v = player.getLocation().getDirection();
			v.multiply(recoil * -1);
			player.setVelocity(v);
		}
	}

	// sends a splash damage attack at enemies in front of the player
	private void spritzAttack(Player player, double damage, float range) {
		Vector looking = player.getLocation().getDirection();
		List<Entity> entities = player.getNearbyEntities(range, range, range);

		for (Entity e : entities) {
			// test if the entity can be damaged and isnt the player
			if (e instanceof LivingEntity && !e.equals(player)) {
				Vector direction = e.getLocation().toVector().subtract(player.getLocation().toVector());
				double angle = looking.angle(direction);

				// scale damage depending on how close you are to the mob
				double distance = player.getLocation().distance(e.getLocation());
				damage -= damage * distance / (range - 1) / 2;

				if (angle < 1) {
					((LivingEntity) e).damage(damage);
					double x = direction.getX() / Math.abs(direction.getX());
					double y = direction.getY();
					double z = direction.getZ() / Math.abs(direction.getZ());
					direction = new Vector(x, y, z);

					direction.multiply(0.5);
					e.setVelocity(direction);
					player.getWorld().playEffect(e.getLocation().add(0,1,0), Effect.SMOKE, 10);
				}
			}
		}
	}

	private void knockbackAttack(Player player, float range) {
		Vector looking = player.getLocation().getDirection();
		List<Entity> entities = player.getNearbyEntities(range, range, range);

		for (Entity e : entities) {
			// test if the entity can be damaged and isnt the player
			if (e instanceof LivingEntity && !e.equals(player)) {
				Vector direction = e.getLocation().toVector().subtract(player.getLocation().toVector());
				double angle = looking.angle(direction);

				// scale damage depending on how close you are to the mob
				double distance = player.getLocation().distance(e.getLocation());
				double kb = 5 - (5 * distance / (range - 1) / 2);

				if (angle < 1) {
					double x = direction.getX() / Math.abs(direction.getX());
					double y = direction.getY();
					double z = direction.getZ() / Math.abs(direction.getZ());
					direction = new Vector(x, y, z);

					direction.multiply(kb);
					e.setVelocity(direction);
				}
			}
		}
	}

	// launch a falling block (duh lol)
	private FallingBlock launchFallingBlock(Player player, Material material, float multiplier, Effect sound) {
		FallingBlock block = player.getWorld().spawnFallingBlock(player.getLocation().add(0,1,0), material, (byte) 0);
		block.setVelocity(player.getEyeLocation().add(0, 1, 0).getDirection().multiply(multiplier));
		player.getWorld().playEffect(player.getLocation(), sound, 1);
		return block;
	}

	// launch a mob
	private Entity launchMobFromSpawnEgg(Player player, EntityType type, float multiplier, Effect sound) {
		Entity mob = (Entity) player.getWorld().spawnEntity(player.getEyeLocation(), type);
		mob.setVelocity(player.getLocation().getDirection().multiply(multiplier));
		player.getWorld().playEffect(player.getLocation(), sound, 1);
		return mob;
	}

	// process click events in the UberItems Shooty Box Guide Menu
	@EventHandler
	private void shootyBoxAmmoGuide(InventoryClickEvent event) {
		// verify that the Player is in a UberItems crafting guide menu
		if (!event.getView().getTitle().contains("Ammo Guide") || event.getView().getTopInventory().getLocation() != null) return;

		// cancel all clicks in this menu
		event.setCancelled(true);

		// close inventory button functionality
		if (Objects.equals(event.getCurrentItem(), MenuUtils.BACK_BUTTON))
			event.getWhoClicked().closeInventory();
	}
	@EventHandler
	private void onCloseInventory(InventoryCloseEvent event) {

		// save ShootyBox inventory on close
		if (event.getView().getTitle().contains("Shooty Box")){
			Player player = (Player) event.getPlayer();
			ItemStack shootyBox = player.getInventory().getItemInMainHand();

			Utilities.saveCompactInventory(shootyBox, event.getInventory().getContents());
			player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1, 1);
		}

		// add items back to a player's inventory if closing the UberItem crafting menu
		if (event.getView().getTitle().contains("UberItems - Craft Item") && event.getView().getTopInventory().getLocation() == null) {
			Inventory i = event.getInventory();
			List<ItemStack> items = Arrays.asList(
					i.getItem(10), i.getItem(11), i.getItem(12),
					i.getItem(19), i.getItem(20), i.getItem(21),
					i.getItem(28), i.getItem(29), i.getItem(30));
			for (ItemStack it : items) if (it != null) Utilities.givePlayerItemSafely((Player)event.getPlayer(), it);

		}

	}
}
