package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.S2CFishCaughtSoundCallback;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class FishingBobberMixin {

    @Shadow private ClientWorld world;

    @Inject(at = @At("HEAD"), method = "onPlaySound")
    private void tickT(PlaySoundS2CPacket packet, CallbackInfo ci) {
        if (packet.getSound() == SoundEvents.ENTITY_FISHING_BOBBER_SPLASH && Enchant.option.autoFishing && world.isClient) {
            Box pos = Box.from(new Vec3d(packet.getX(), packet.getY(), packet.getZ()));
            S2CFishCaughtSoundCallback.EVENT.invoker().isCaught(world, pos);
        }
    }
}
