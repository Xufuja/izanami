package dev.xfj.core;

import java.util.*;

public class LayerStack {
    private final List<Layer> layers;
    private int layerInsertIndex;

    public LayerStack() {
        layers = new ArrayList<>();
        layerInsertIndex = 0;
    }

    public void pushLayer(Layer layer) {
        layers.add(layerInsertIndex, layer);
        layerInsertIndex++;
        layer.onAttach();
    }

    public void pushOverlay(Layer overlay) {
        layers.add(overlay);
        overlay.onAttach();
    }

    public void popLayer(Layer layer) {
        Optional<Integer> index = findIndex(layer);
        index.ifPresent(i -> {
            layers.remove((int) i);
            layerInsertIndex--;
        });
        layer.onDetach();
    }

    public void popOverlay(Layer overlay) {
        Optional<Integer> index = findIndex(overlay);
        index.ifPresent(i -> layers.remove((int) i));
        overlay.onDetach();
    }

    public List<Layer> getLayers() {
        return layers;
    }

    private Optional<Integer> findIndex(Layer layer) {
        int index = layers.indexOf(layer);
        return (index != -1) ? Optional.of(index) : Optional.empty();
    }
}
