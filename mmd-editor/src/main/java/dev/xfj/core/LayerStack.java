package dev.xfj.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class LayerStack {
    private List<Layer> layers;
    private int layerInsertIndex;

    public LayerStack() {
        layers = new ArrayList<>();
        layerInsertIndex = 0;
    }

    public void pushLayer(Layer layer) {
        layers.add(layer);
        layerInsertIndex++;
    }

    public void pushOverlay(Layer overlay) {
        layers.add(overlay);
    }

    public void popLayer(Layer layer) {
        layers.remove(layer);
        layerInsertIndex--;
    }

    public void popOverlay(Layer overlay) {
        layers.remove(overlay);
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public int getLayerInsertIndex() {
        return layerInsertIndex;
    }
}
