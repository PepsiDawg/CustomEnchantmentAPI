package io.github.pepsidawg.enchantmentapi.nms.v1_12_R1;

import io.github.pepsidawg.api.NMS;
import io.github.pepsidawg.api.EnchantmentDetails;
import io.github.pepsidawg.api.NMSLookupResponse;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NMSHandler implements NMS {
    public ItemStack setEnchants(ItemStack item, Map<String, Integer> enchantments) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        NBTTagList nmsEnchantList = new NBTTagList();

        for(Map.Entry<String, Integer> entry : enchantments.entrySet()) {
            NBTTagCompound temp = new NBTTagCompound();
            temp.setString("id", entry.getKey());
            temp.setInt("level", entry.getValue());
            nmsEnchantList.add(temp);
        }

        tag.set("customEnchantments", nmsEnchantList);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public EnchantmentDetails get(ItemStack item, String enchantment) {
        NMSLookupResponse response = find(item, enchantment);

        if(response.found) {
            net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
            NBTTagCompound tag = nmsItem.getTag();
            NBTTagList enchantments = (NBTTagList) tag.get("customEnchantments");
            NBTTagCompound enchant = enchantments.get(response.index);

            return new EnchantmentDetails(enchant.getString("id"), enchant.getInt("level"));
        }
        return null;
    }

    public NMSLookupResponse find(ItemStack item, String enchantment) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        NBTTagList enchantments = tag.hasKey("customEnchantments") ? (NBTTagList) tag.get("customEnchantments") : new NBTTagList();

        for(int index = 0; index < enchantments.size(); index++) {
            NBTTagCompound enchant = enchantments.get(index);

            if(enchant.getString("id").equalsIgnoreCase(enchantment)) {
                return new NMSLookupResponse(true, index);
            }
        }
        return new NMSLookupResponse(false, -1);
    }

    public Map<String, Integer> getEnchants(ItemStack item) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        NBTTagList nmsEnchantList = tag.hasKey("customEnchantments") ? (NBTTagList) tag.get("customEnchantments") : new NBTTagList();
        Map<String, Integer> enchants = new HashMap<String, Integer>();

        for(int index = 0; index < nmsEnchantList.size(); index++) {
            NBTTagCompound enchant = nmsEnchantList.get(index);
            enchants.put(enchant.getString("id"), enchant.getInt("level"));
        }

        return enchants;
    }

    public ItemStack generateItemUUID(ItemStack item) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        UUID uuid = UUID.randomUUID();

        tag.set("itemUUID", new NBTTagString(uuid.toString()));
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public UUID getItemUUID(ItemStack item) {
        if(hasItemUUID(item)) {
            net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
            NBTTagCompound tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

            return UUID.fromString(tag.getString("itemUUID"));
        }

        return null;
    }

    public boolean hasItemUUID(ItemStack item) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        return tag.hasKey("itemUUID");
    }

}
