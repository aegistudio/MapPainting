
package net.aegistudio.mpp.export;

public interface PluginCanvasRegistry<T extends PluginCanvas> {
    public String plugin();

    public String identifier();

    public PluginCanvasFactory<T> factory();

    public T canvas();

    public int mapid();

    public String name();
}

