package dev.mariany.genesis;

import dev.mariany.genesis.world.level.gamerules.GenesisGameRuleDefaults;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public final class GenesisPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        GenesisGameRuleDefaults.bootstrap();
    }
}
