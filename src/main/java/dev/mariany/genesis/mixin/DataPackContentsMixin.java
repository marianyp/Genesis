package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.age.AgeDataLoader;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.command.CommandManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(DataPackContents.class)
public class DataPackContentsMixin {
    @Unique
    private AgeDataLoader ageLoader;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(
            CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries,
            RegistryWrapper.WrapperLookup registries,
            FeatureSet enabledFeatures,
            CommandManager.RegistrationEnvironment environment,
            List<Registry.PendingTagLoad<?>> pendingTagLoads,
            int functionPermissionLevel,
            CallbackInfo ci
    ) {
        this.ageLoader = new AgeDataLoader(registries);
    }

    @WrapOperation(method = "getContents", at = @At(value = "INVOKE", target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"))
    public List<ResourceReloader> wrapGetContents(Object first, Object second, Object third, Operation<List<ResourceReloader>> original) {
        List<ResourceReloader> resourceReloaders = new ArrayList<>(List.of(this.ageLoader));
        resourceReloaders.addAll(original.call(first, second, third));
        return resourceReloaders;
    }
}
