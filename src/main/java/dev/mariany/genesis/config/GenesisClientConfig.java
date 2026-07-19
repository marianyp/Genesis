package dev.mariany.genesis.config;

import java.awt.*;

public class GenesisClientConfig {
    public TirednessWidgetConfig tirednessWidget = new TirednessWidgetConfig();

    public static class TirednessWidgetConfig {
        public boolean enabled = true;
        public Point position = new Point(80, 10);
    }
}
