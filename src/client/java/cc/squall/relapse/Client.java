package cc.squall.relapse;

import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.MixinEnvironment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client implements ClientModInitializer {
    public static final String MOD_ID = "relapse";
    @Getter
    public String clientName = "relapse";

    @Getter
    public boolean devMode = true;
    public static Minecraft mc;
    public static final Logger log;
    public static final ModMetadata modMeta;
    public static final String name;
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
    }
}
