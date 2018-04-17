package io.github.pepsidawg.enchantmentfun;

import io.github.pepsidawg.enchantmentfun.enchantments.AutoSmeltEnchantment;
import io.github.pepsidawg.enchantmentfun.enchantments.LifeStealEnchantment;
import io.github.pepsidawg.enchantmentfun.enchantments.TestEnchantment;
import io.github.pepsidawg.plugin.EnchantmentManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantmentFun extends JavaPlugin {
    private EnchantmentManager manager;

    @Override
    public void onEnable() {
        manager = EnchantmentManager.getInstance();

        try {
            manager.registerEnchantment(new AutoSmeltEnchantment());
            manager.registerEnchantment(new TestEnchantment());
            manager.registerEnchantment(new LifeStealEnchantment());
        } catch (Exception e) {
            getLogger().info(e.getMessage());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(command.getName().equalsIgnoreCase("testenchant")) {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();

            try {
                ItemStack result = manager.enchantItem(item, manager.getEnchantByName("life_steal"), 1, false);
                player.getInventory().setItemInMainHand(result);
                player.sendMessage(ChatColor.GREEN + "Item enchanted!");
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
                e.printStackTrace();
            }
        }

        return true;
    }
}
