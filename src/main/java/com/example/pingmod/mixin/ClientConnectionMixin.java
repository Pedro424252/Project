package com.example.pingmod.mixin;

import com.example.pingmod.PacketDelayer;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {

    @Shadow
    protected abstract void sendImmediately(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush);

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void pingmod$delay(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        if (!PacketDelayer.shouldDelay()) return;
        ci.cancel();
        PacketDelayer.schedule(() -> this.sendImmediately(packet, callbacks, flush));
    }
}
