package com.doo.xenchant.util;

import com.doo.xenchant.Enchant;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * 附魔工具
 */
public class NetworkUtil {

    private static final Identifier INC_ITEM_DAMAGE = new Identifier(Enchant.ID, "mod_item_attr");

    public static void incItemStackDamage(int old, int inc, int slot, ItemStack item) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeItemStack(item);
        buf.writeInt(slot);
        buf.writeInt(old);
        buf.writeInt(inc);
        ClientPlayNetworking.send(INC_ITEM_DAMAGE, buf);
    }

    public static void registerAll() {
        // server listener
        ServerPlayNetworking.registerGlobalReceiver(INC_ITEM_DAMAGE, (server, player, h, buf, sender) -> {
            ItemStack clientItem = buf.readItemStack();
            int slot = buf.readInt();
            int old = buf.readInt();
            int inc = buf.readInt();
            ItemStack serverItem = player.getInventory().getStack(slot);
            if (serverItem.isItemEqual(clientItem) && serverItem.getDamage() == old) {
                serverItem.setDamage(old + inc);
            }
        });
    }
}
