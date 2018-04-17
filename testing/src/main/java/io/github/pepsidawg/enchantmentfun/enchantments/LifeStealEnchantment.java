package io.github.pepsidawg.enchantmentfun.enchantments;

import io.github.pepsidawg.api.EnchantmentDetails;
import io.github.pepsidawg.plugin.CustomEnchantment;
import io.github.pepsidawg.plugin.EnchantmentManager;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class LifeStealEnchantment extends CustomEnchantment {
    private EnchantmentManager manager;
    private final double HEAL_PERCENTAGE = 0.05;

    public LifeStealEnchantment() {
        super("LIFE_STEAL", "Life Steal", EnchantmentManager.getInstance().getNextID());
        manager = EnchantmentManager.getInstance();
        setMaxLevel(3);
        addEnchantmentTarget(EnchantmentTarget.WEAPON);
        addEnchantmentTarget(EnchantmentTarget.BOW);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player || event.getDamager() instanceof Arrow) {
            Player player;
            ItemStack item;

            if(event.getDamager() instanceof  Arrow) {
                Arrow arrow = (Arrow)event.getDamager();
                if(arrow.getShooter() instanceof Player) {
                    player = (Player) arrow.getShooter();
                } else {
                    return;
                }
            } else {
                player = (Player) event.getEntity();
            }

            item = player.getInventory().getItemInMainHand();

            if(manager.hasCustomEnchantment(item, this.getEnchantmentName())) {
                EnchantmentDetails details = manager.getEnchantmentDetails(item, this.getEnchantmentName());
                double healAmount = event.getFinalDamage() * (HEAL_PERCENTAGE * details.level);
                healAmount = (player.getHealth() + healAmount) > player.getMaxHealth() ? player.getMaxHealth() : (player.getHealth() + healAmount);
                player.setHealth(healAmount);
                spawnHealthIndicator(event.getEntity().getLocation(), 5);
            }
        }
    }

    private void spawnHealthIndicator(Location location, int amount) {
        for(int i = 0; i < amount; i++) {
            location.getWorld().spigot().playEffect(location.add(0.5, 1, 0.5), Effect.COLOURED_DUST, 0, 0, 1 , .3f, .3f, 1, 0, 16);
        }
    }
}
