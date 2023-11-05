
package net.aegistudio.mpp.script;

import java.util.ArrayList;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import net.aegistudio.mpp.control.ConcreteControlCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ScriptDebugCommand
extends ConcreteControlCommand {
    public static final String SPACING = "    ";
    public static final String FLAG_PREFIX = ChatColor.YELLOW.toString() + (Object)ChatColor.BOLD;
    public static final String DESCRIPTION_PREFIX = ChatColor.RESET.toString();
    public static final String NOT_SCRIPT = "notScript";
    public String notScript;

    public ScriptDebugCommand() {
        this.description = "@control.debug.description";
        this.paramList = "[@<flags> [<argument>...]]";
        this.notScript = (Object)ChatColor.RED + "@control.debug.notScript";
    }

    @Override
    protected void subhandle(MapPainting painting, MapCanvasRegistry canvas, CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            sender.sendMessage("Please specify what to do, using the " + FLAG_PREFIX + "flags" + DESCRIPTION_PREFIX + ":");
            sender.sendMessage(SPACING + FLAG_PREFIX + "@e" + DESCRIPTION_PREFIX + ": erase the cassette, left no record.");
            sender.sendMessage(SPACING + FLAG_PREFIX + "@r" + DESCRIPTION_PREFIX + ": reboot the script with specified arguments.");
            sender.sendMessage(SPACING + FLAG_PREFIX + "@l" + DESCRIPTION_PREFIX + ": reload the script from file system.");
            sender.sendMessage(SPACING + FLAG_PREFIX + "@R" + DESCRIPTION_PREFIX + ": (=@rel) restart the script completely.");
        } else {
            if (!(canvas.canvas instanceof ScriptCanvas)) {
                sender.sendMessage(this.notScript.replace("$canvasName", canvas.name));
                return;
            }
            ScriptCanvas scriptCanvas = (ScriptCanvas)canvas.canvas;
            boolean rebootScript = false;
            boolean reloadScript = false;
            boolean eraseCassette = false;
            ArrayList<String> paramList = new ArrayList<String>();
            for (String arg : arguments) {
                if (arg.startsWith("@")) {
                    char[] carray = arg.toCharArray();
                    for (int i = 1; i < carray.length; ++i) {
                        if (carray[i] == 'e') {
                            eraseCassette = true;
                        }
                        if (carray[i] == 'l') {
                            reloadScript = true;
                        }
                        if (carray[i] == 'r') {
                            rebootScript = true;
                        }
                        if (carray[i] != 'R') continue;
                        eraseCassette = true;
                        reloadScript = true;
                        rebootScript = true;
                    }
                    continue;
                }
                paramList.add(arg);
            }
            if (reloadScript) {
                try {
                    scriptCanvas.engine = null;
                    scriptCanvas.setEngine();
                    scriptCanvas.setScript();
                    sender.sendMessage("The script has been successfully reloaded!");
                }
                catch (Exception e) {
                    sender.sendMessage(e.getMessage());
                    return;
                }
            }
            if (eraseCassette) {
                scriptCanvas.cassette.clear();
                sender.sendMessage("The cassette has been reset.");
            }
            if (rebootScript) {
                try {
                    scriptCanvas.reboot(paramList.toArray(new String[0]));
                    sender.sendMessage("The script has been reboot.");
                }
                catch (Exception e) {
                    sender.sendMessage(e.getMessage());
                    return;
                }
            }
            painting.ackHistory(canvas, sender);
        }
    }
}

