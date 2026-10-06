package cc.squall.relapse.modules.impl.misc;


import cc.squall.relapse.Client;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;

import cc.squall.relapse.events.EventChatSend;
import meteordevelopment.orbit.EventHandler;

public class ClientCommands extends cc.squall.relapse.modules.Module {
    public ClientCommands() {
        super("ClientCommands", "Enables dot commands for client", cc.squall.relapse.modules.Module.Category.Misc);
    }
    @EventHandler
    private void onChatSend(EventChatSend event) {
        String msg = event.getMessage();
        if (msg.length() < 2 || !msg.startsWith(".") || msg.charAt(1) == '.') return; // let "..." through
        event.cancel();
        onDotCommand(msg);
    }
    // bad port from kayasaka, which is a bad port from nebula

    public void onDotCommand(String command) {
        String[] args = command.substring(1).trim().split("\\s+");

        switch (args[0].toLowerCase()) {
            case "list":
                list();
                break;
            case "enable":
                setState(args, true);
                break;
            case "disable":
                setState(args, false);
                break;
            default:
                toggle(args[0]);
        }
    }

    private void list() {
        StringBuilder names = new StringBuilder();
        for (cc.squall.relapse.modules.Module m : Client.getInstance().getModman().getModules()) {
            if (!names.isEmpty()) names.append(", ");
            names.append(m.getName()).append(m.isEnabled() ? " [on]" : "");
        }
        print(names.toString());
    }

    private void setState(String[] args, boolean enabled) {
        cc.squall.relapse.modules.Module m = module(args, 1, ".enable/.disable <module>");
        if (m == null) return;
        if (m.isEnabled() != enabled) m.toggle();
        print(m.getName() + (enabled ? " enabled" : " disabled"));
    }

    private void toggle(String name) {
        cc.squall.relapse.modules.Module m = find(name);
        if (m == null) {
            print("unknown command: ." + name + " (list, enable, disable, settings, set, bind, or a module name)");
            return;
        }
        m.toggle();
        print(m.getName() + (m.isEnabled() ? " enabled" : " disabled"));
    }
    /*
    private void settings(String[] args) {
        Module m = module(args, 1, ".settings <module>");
        if (m == null) return;
        if (m.getSettings().isEmpty()) {
            print(m.getName() + " has no settings");
            return;
        }
        for (Setting s : m.getSettings()) {
            print(s.getName() + " = " + s.getValueString());
        }
    }

    private void set(String[] args) {
        Module m = module(args, 1, ".set <module> <setting> <value>");
        if (m == null) return;
        if (args.length < 4) {
            print("usage: .set <module> <setting> <value>");
            return;
        }
        for (Setting s : m.getSettings()) {
            if (s.getName().equalsIgnoreCase(args[2])) {
                print(s.setValueString(args[3]) ? s.getName() + " = " + s.getValueString() : "invalid value: " + args[3]);
                return;
            }
        }
        print("setting not found: " + args[2]);
    }

    private void bind(String[] args) {
        if (args.length >= 2 && args[1].equalsIgnoreCase("list")) {
            StringBuilder bound = new StringBuilder();
            for (Module m : Module.getRegistry()) {
                if (m.getKeybind() <= 0) continue;
                if (bound.length() > 0) bound.append(", ");
                bound.append(m.getName()).append(" = ").append(keyName(m.getKeybind()));
            }
            print(bound.length() == 0 ? "nothing is bound" : bound.toString());
            return;
        }

        Module m = module(args, 1, ".bind <module> [key|none]   or   .bind list");
        if (m == null) return;
        if (args.length < 3) {
            print(m.getName() + " is bound to " + (m.getKeybind() <= 0 ? "nothing" : keyName(m.getKeybind())));
            return;
        }

        int key = parseKey(args[2]);
        if (key == -1) {
            print("unknown key: " + args[2] + " (try a name like RSHIFT, a number, or none)");
            return;
        }
        m.setKeybind(key);
        print(m.getName() + " bound to " + (key == 0 ? "nothing" : keyName(key)));
    }

    private static int parseKey(String token) {
        if (token.equalsIgnoreCase("none") || token.equalsIgnoreCase("unbind")) return 0;
        try {
            int raw = Integer.parseInt(token);
            return raw > 0 ? raw : -1;
        } catch (NumberFormatException ignored) {
            return KeyUtil.findKeycode("KEY_" + token.toUpperCase(Locale.ROOT));
        }
    }

    private static String keyName(int code) {
        String name = KeyUtil.findKeyString(code);
        return name == null ? String.valueOf(code) : name.replace("KEY_", "");
    }

     */

    private static cc.squall.relapse.modules.Module find(String name) {
        return Client.getInstance().getModman().getModule(name);
    }

    private cc.squall.relapse.modules.Module module(String[] args, int index, String usage) {
        if (args.length <= index) {
            print("usage: " + usage);
            return null;
        }
        cc.squall.relapse.modules.Module m = find(args[index]);
        if (m == null) print("module not found: " + args[index]);
        return m;
    }

    private void print(String message) {
        mc.execute(() ->
                mc.gui.hud.getChat().addClientSystemMessage(
                        // todo: add color options once settings are made
                        Component.literal("[relapse] ").withStyle(ChatFormatting.AQUA)
                                .append(Component.literal(message).withStyle(ChatFormatting.WHITE))
                )
        );
    }
}