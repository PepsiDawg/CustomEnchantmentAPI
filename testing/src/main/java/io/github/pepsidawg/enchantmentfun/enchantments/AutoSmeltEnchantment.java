package io.github.pepsidawg.enchantmentfun.enchantments;

import io.github.pepsidawg.plugin.CustomEnchantment;
import io.github.pepsidawg.plugin.EnchantmentManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class AutoSmeltEnchantment extends CustomEnchantment {
    private EnchantmentManager manager;

    public AutoSmeltEnchantment() {
        super("AUTO_SMELT", "Auto Smelting", EnchantmentManager.getInstance().getNextID());
        manager = EnchantmentManager.getInstance();
        addEnchantmentTarget(EnchantmentTarget.TOOL);
        addConflictingEnchantment("SILK_TOUCH");
        addConflictingEnchantment("LOOT_BONUS_BLOCK");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if(manager.hasCustomEnchantment(item, this.getEnchantmentName())) {
            Location location = event.getBlock().getLocation().clone().add(0.5, 0.5 ,0.5);
            World world = location.getWorld();


            for(ItemStack drop : event.getBlock().getDrops()) {
                if (SmeltingResult.shouldProc(drop.getType())) {
                    SmeltingResult result = SmeltingResult.valueOf(drop.getType().name().toUpperCase());
                    world.dropItemNaturally(location, result.drop);
                    world.spawnParticle(Particle.LAVA, location, 5);
                    ((ExperienceOrb) world.spawnEntity(location, EntityType.EXPERIENCE_ORB)).setExperience(result.experience);
                } else {
                    world.dropItemNaturally(location, drop);
                }
            }
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
        }
    }


    private enum SmeltingResult {
        COBBLESTONE(Material.STONE, 0, 1),
        LOG(Material.COAL, 1, 1),
        LOG_2(Material.COAL, 1, 1),
        NETHERRACK(Material.NETHER_BRICK_ITEM, 0, 1),
        GRAVEL(Material.SAND, 0, 1),
        SAND(Material.GLASS, 0, 1),
        CHORUS_FLOWER(Material.CHORUS_FRUIT_POPPED, 0, 1),
        CLAY(Material.HARD_CLAY, 0, 1),
        IRON_ORE(Material.IRON_INGOT, 0, 3),
        GOLD_ORE(Material.GOLD_INGOT, 0, 3);

        int experience;
        ItemStack drop;

        SmeltingResult(Material mat, int data, int experience) {
            drop = new ItemStack(mat, 1, (short)data);
            this.experience = experience;
        }

        static boolean shouldProc(Material mat) {
            for(SmeltingResult sr : SmeltingResult.values()) {
                if(sr.name().equals(mat.name())) {
                    return true;
                }
            }
            return false;
        }
    }
}
