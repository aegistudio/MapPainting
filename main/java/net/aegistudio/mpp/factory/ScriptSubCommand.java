/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 */
package net.aegistudio.mpp.factory;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.script.ScriptCanvas;
import net.aegistudio.mpp.script.ScriptEnginePool;
import net.aegistudio.mpp.script.UnsupportedException;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class ScriptSubCommand
extends ConcreteCreateSubCommand {
    public static final String NO_SCRIPT = "noScript";
    public String noScript;
    public static final String LANGUAGE_UNSUPPORTED = "languageUnsupported";
    public String languageUnsupported;
    public static final String PROMPT_ENGINE = "promptEngine";

    public ScriptSubCommand() {
        this.description = "@create.script.description";
        this.paramList = "<script> [<argument>...]";
        this.noScript = "@create.script.noScript";
        this.languageUnsupported = "@create.script.languageUnsupported";
    }

    @Override
    protected Canvas create(MapPainting painting, CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            sender.sendMessage(this.noScript);
            return null;
        }
        try {
            ScriptCanvas canvas = new ScriptCanvas(painting);
            canvas.filename = arguments[0];
            canvas.setEngine();
            canvas.setScript();
            String[] subarguments = new String[arguments.length - 1];
            System.arraycopy(arguments, 1, subarguments, 0, arguments.length - 1);
            canvas.reboot(subarguments);
            return canvas;
        }
        catch (UnsupportedException e) {
            sender.sendMessage(this.languageUnsupported.replace("$language", e.getMessage()));
            return null;
        }
        catch (Exception e) {
            sender.sendMessage(e.toString());
            return null;
        }
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        super.load(painting, section);
        this.noScript = painting.getLocale(NO_SCRIPT, this.noScript, section);
        this.languageUnsupported = painting.getLocale(LANGUAGE_UNSUPPORTED, this.languageUnsupported, section);
        if (!section.contains(PROMPT_ENGINE)) {
            section.createSection(PROMPT_ENGINE);
        }
        ConfigurationSection promptEngines = section.getConfigurationSection(PROMPT_ENGINE);
        for (String key : promptEngines.getKeys(false)) {
            ScriptEnginePool.replace(key, promptEngines.getString(key));
        }
    }
}

