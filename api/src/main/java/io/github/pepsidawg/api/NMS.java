package io.github.pepsidawg.api;

import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public interface NMS {
    ItemStack setEnchants(ItemStack item, Map<String, Integer> enchantments);
    EnchantmentDetails get(ItemStack item, String enchantment);
    NMSLookupResponse find(ItemStack item, String enchantment);
    Map<String, Integer> getEnchants(ItemStack item);
    ItemStack generateItemUUID(ItemStack item);
    UUID getItemUUID(ItemStack item);
    boolean hasItemUUID(ItemStack item);
  }
