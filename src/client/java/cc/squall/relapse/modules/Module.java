package cc.squall.relapse.modules;

import cc.squall.relapse.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

public abstract class Module {
    @Getter
    @Setter
    public boolean enabled;
    @Getter
    public String description;
    @Getter
    public String name;
    @Getter
    public Category category;
    @Getter
    protected final Minecraft mc = Minecraft.getInstance();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
    }
    public Module(String name, Category category) {
        this.name = name;
        this.description = "A module";
        this.category = category;
        this.enabled = false;
    }

    public void onUpdate() {}
    public void onEnable() {}
    public void onDisable() {}
    public void onToggle() {}
    public void toggle() {
        enabled = !enabled;
    }
    public void enable() {
        enabled = true;
    }
    public void disable() {
        enabled = false;
    }
    public boolean isDisabled() {
        return !isEnabled();
    }


    public enum Category {
        Combat, Movement, Render, Player, Misc
    }
}
