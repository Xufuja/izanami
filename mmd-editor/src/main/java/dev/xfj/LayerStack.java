package dev.xfj;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class LayerStack {
    private List<Layer> layers;
    private ListIterator<Layer> layerInsert;

    public LayerStack() {
        layers = new ArrayList<>();
        layerInsert = layers.listIterator();
    }

    public void pushLayer(Layer layer) {
        layerInsert.add(layer);
        layerInsert.previous();
    }

    public void pushOverlay(Layer overlay) {
        layers.add(overlay);
    }

    public void popLayer(Layer layer) {
        Objects.requireNonNull(layer);
        layers.remove(layer);
        layerInsert.previous();
    }

    public void popOverlay(Layer overlay) {
        Objects.requireNonNull(overlay);
        layers.remove(overlay);
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public ListIterator<Layer> getLayerInsert() {
        return layerInsert;
    }
}
