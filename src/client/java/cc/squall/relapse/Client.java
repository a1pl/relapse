package cc.squall.relapse;

import cc.squall.relapse.modules.ModuleManager;
import cc.squall.relapse.modules.impl.misc.ClientCommands;
import lombok.Getter;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.MixinEnvironment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client implements ClientModInitializer {
    public static final String MOD_ID = "relapse";
    @Getter
    public String clientName = "relapse";

    @Getter
    public final IEventBus EVENT_BUS = new EventBus();

    @Getter
    public boolean devMode = true;
    @Getter
    public static Minecraft mc;
    public static final Logger log;
    public static final ModMetadata modMeta;
    public static final String name;
    @Getter
    public ModuleManager modman;
    @Getter
    private static Client instance;
    //meteor paste
    static {
        modMeta = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata();

        name = modMeta.getName();
        log = LoggerFactory.getLogger(name);
        String versionString = modMeta.getVersion().getFriendlyString();
        // dont use intellij play, use gradle through intellij instead
        if (versionString.contains("-")) versionString = versionString.split("-")[0];
    }
    @Override
    public void onInitializeClient() {
        mc = Minecraft.getInstance();
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            log.info("Force loading mixins");
            MixinEnvironment.getCurrentEnvironment().audit();
        }

        modman = new ModuleManager();

        if (devMode) {
            modman.getModulesMap().put("ClientCommands", new ClientCommands());
            while (true) {
                mc.execute(() ->
                        mc.gui.hud.getChat().addClientSystemMessage(
                                // todo: add color options once settings are made
                                Component.literal("[relapse] ").withStyle(ChatFormatting.AQUA)
                                        .append(Component.literal("welcome to java, brother").withStyle(ChatFormatting.WHITE))
                        )
                );
            }
        }
    }
}
