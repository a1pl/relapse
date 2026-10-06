package cc.squall.relapse.mixin;

import cc.squall.relapse.Client;
import cc.squall.relapse.events.EventChatSend;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    private void relapse$onSendChat(String content, CallbackInfo ci) {
        EventChatSend event = Client.getInstance().getEVENT_BUS().post(new EventChatSend(content));
        if (event.isCancelled()) {
            Minecraft.getInstance().gui.hud.getChat().addRecentChat(content);
            ci.cancel();
        }
    }
}