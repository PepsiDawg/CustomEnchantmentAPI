package io.github.pepsidawg.enchantmentapi;

import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AnvilHandler implements Listener {
    private List<String> ghostItem;

    public AnvilHandler() {
        ghostItem = new ArrayList<String>();
    }

    @EventHandler
    public void prepareAnvil(PrepareAnvilEvent event) {
        ItemStack contents[] = event.getInventory().getContents();
        ItemStack target = contents[0] != null ? contents[0] : new ItemStack(Material.AIR);
        ItemStack sacrifice = contents[1] != null ? contents[1] : new ItemStack(Material.AIR);
        ItemStack result = event.getResult();

        if(validateItem(target)) {
            Pair<ItemStack, Integer> combineResult = null;
            boolean tarEnchanted = EnchantmentManager.hasCustomEnchantment(target);
            boolean sacEnchanted = EnchantmentManager.hasCustomEnchantment(sacrifice);
            boolean applyEnchCost = false;

            if(sacrifice.getType().equals(Material.ENCHANTED_BOOK) && (tarEnchanted || sacEnchanted)) { //book
                combineResult = EnchantmentManager.combine(target, sacrifice, result);
                applyEnchCost = true;
            } else if(target.getType().equals(sacrifice.getType()) && (tarEnchanted || sacEnchanted)) { //repair with same item / combine
                combineResult = EnchantmentManager.combine(target, sacrifice, result);
                applyEnchCost = sacEnchanted;
            } else if(tarEnchanted && validateItem(result)) { //repair with mat / rename && valid
                combineResult = EnchantmentManager.combine(target, sacrifice, result);
            }

            if(combineResult != null) {
                String code = String.valueOf(event.getInventory().hashCode());
                if(!ghostItem.contains(code)) {
                    ghostItem.add(code);
                }

                if(applyEnchCost) {
                    event.getInventory().setRepairCost(event.getInventory().getRepairCost() + combineResult.getValue()*2);
                }

                CustomEnchantmentChangedEvent enchantEvent = new CustomEnchantmentChangedEvent(target, sacrifice, combineResult.getKey(), CustomEnchantmentChangedEvent.EnchantmentChangeReason.ANVIL);
                Bukkit.getPluginManager().callEvent(enchantEvent);
                event.setResult(enchantEvent.getResult());
            }
        }
    }

    @EventHandler
    public void anvilClick(InventoryClickEvent event) {
        if(event.getInventory() instanceof AnvilInventory) {
            Bukkit.getLogger().info(ghostItem.size() + "");
            String code = String.valueOf(event.getInventory().hashCode());
            if(ghostItem.contains(code) && event.getRawSlot() == 2) {
                Player player = (Player) event.getWhoClicked();
                event.setCursor(event.getInventory().getItem(2));
                ghostItem.remove(code);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                event.getInventory().clear();
            }
        }
    }

    private boolean validateItem(ItemStack item) {
        return item != null && !item.getType().equals(Material.AIR);
    }
}
