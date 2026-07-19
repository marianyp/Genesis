package dev.mariany.genesis.client.gui.screen;

import dev.mariany.genesis.client.GenesisClient;
import dev.mariany.genesis.client.gui.screen.tiredness.TirednessWidget;
import dev.mariany.genesis.config.GenesisClientConfig;
import dev.mariany.genesis.mixin.accessor.AbstractContainerScreenAccessor;
import dev.mariany.genesis.mixin.accessor.ScreenAccessor;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GenesisWidgets {
    private static final Map<Class<? extends Screen>, WidgetProvider> WIDGETS = new HashMap<>();

    static {
        registerTirednessWidget();
    }

    private static void registerTirednessWidget() {
        GenesisClientConfig config = GenesisClient.getConfig();
        GenesisClientConfig.TirednessWidgetConfig tirednessWidgetConfig = config.tirednessWidget;

        if (!tirednessWidgetConfig.enabled) {
            return;
        }

        Point position = tirednessWidgetConfig.position;

        register(
                InventoryScreen.class,
                (pointSupplier) -> new TirednessWidget(offsetPointSupplier(pointSupplier, position))
        );
    }

    private static void register(Class<? extends Screen> screenClass, WidgetProvider widgetProvider) {
        WIDGETS.put(screenClass, widgetProvider);
    }

    private static Supplier<Point> offsetPointSupplier(Supplier<Point> pointSupplier, Point point) {
        return offsetPointSupplier(pointSupplier, point.x, point.y);
    }

    private static Supplier<Point> offsetPointSupplier(Supplier<Point> pointSupplier, int x, int y) {
        return () -> new Point((int) (pointSupplier.get().getX() + x), (int) (pointSupplier.get().getY() + y));
    }

    public static void bootstrap() {
        ScreenEvents.AFTER_INIT.register(GenesisWidgets::onAfterScreenInit);
    }

    private static void onAfterScreenInit(Minecraft minecraft, Screen screen, int width, int height) {
        for (Map.Entry<Class<? extends Screen>, WidgetProvider> entry : WIDGETS.entrySet()) {
            Class<? extends Screen> screenClass = entry.getKey();
            WidgetProvider widgetProvider = entry.getValue();

            if (!screenClass.isInstance(screen)) {
                continue;
            }

            if (!(screen instanceof AbstractContainerScreen<?> abstractContainerScreen)) {
                continue;
            }


            Supplier<Point> pointSupplier = () -> {
                int x = ((AbstractContainerScreenAccessor) abstractContainerScreen).genesis$leftPos();
                int y = ((AbstractContainerScreenAccessor) abstractContainerScreen).genesis$topPos();
                return new Point(x, y);
            };

            AbstractWidget widget = widgetProvider.get(pointSupplier);

            ((ScreenAccessor) screen).genesis$addRenderableWidget(widget);
        }
    }

    @FunctionalInterface
    interface WidgetProvider {
        AbstractWidget get(Supplier<Point> pointSupplier);
    }
}
