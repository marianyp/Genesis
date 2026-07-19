package dev.mariany.genesis.screen;

import dev.mariany.genesis.Genesis;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class GenesisScreenHandlers {
    public static final MenuType<KilnScreenHandler> KILN = register("kiln", KilnScreenHandler::new);

    public static final MenuType<AssemblyScreenHandler> ASSEMBLY = register(
            "assembly",
            AssemblyScreenHandler::new
    );

    private static <T extends AbstractContainerMenu> MenuType<T> register(String id, MenuType.MenuSupplier<T> factory) {
        return Registry.register(
                BuiltInRegistries.MENU,
                Genesis.id(id),
                new MenuType<>(factory, FeatureFlags.VANILLA_SET)
        );
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Screen Handlers");
    }
}
