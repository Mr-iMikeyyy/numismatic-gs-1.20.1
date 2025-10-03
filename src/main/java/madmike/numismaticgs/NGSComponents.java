package madmike.numismaticgs;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import madmike.numismaticgs.components.scoreboard.PlayerLevelsComponent;
import madmike.numismaticgs.components.scoreboard.PlayerNamesComponent;
import net.minecraft.util.Identifier;

public class NGSComponents implements ScoreboardComponentInitializer {
    public NGSComponents() {}

    private static Identifier id(String path) {
        return new Identifier(NGSServer.MOD_ID, path);
    }


    /* -------------------------------------------------------
     * Scoreboard-scoped Components (server-wide/stateful)
     * ----------------------------------------------------- */

    public static final ComponentKey<PlayerLevelsComponent> PLAYER_LEVELS =
            ComponentRegistryV3.INSTANCE.getOrCreate(id("player_levels"), PlayerLevelsComponent.class);

    public static final ComponentKey<PlayerNamesComponent> PLAYER_NAMES =
            ComponentRegistryV3.INSTANCE.getOrCreate(id("player_names"), PlayerNamesComponent.class);


    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry scoreboardComponentFactoryRegistry) {
        scoreboardComponentFactoryRegistry.registerScoreboardComponent(PLAYER_LEVELS, PlayerLevelsComponent::new);
        scoreboardComponentFactoryRegistry.registerScoreboardComponent(PLAYER_NAMES, PlayerNamesComponent::new);
    }
}
