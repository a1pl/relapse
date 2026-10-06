package cc.squall.relapse.modules;

import cc.squall.relapse.utils.datatypes.collections.BiMap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    @Getter
    private final BiMap<String, Module> modulesMap = new BiMap<>();

    public void enable(String moduleName) {
        Module current = getModule(moduleName);
        current.enable();
        current.onEnable();
        current.onToggle();
    }
    public void disable(String moduleName) {
        Module current = getModule(moduleName);
        current.disable();
        current.onDisable();
        current.onToggle();
    }

    public boolean isEnabled(String moduleName) {
        return getModule(moduleName).isEnabled();
    }
    public boolean isDisabled(String moduleName) {
        return getModule(moduleName).isDisabled();
    }

    public List<Module> getModules() {
        return new ArrayList<>(modulesMap.getValues());
    }
    public Module getModule(String moduleName) {
        return modulesMap.get(moduleName);
    }
}
