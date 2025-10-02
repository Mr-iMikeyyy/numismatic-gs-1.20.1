package madmike.numismaticgs;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import madmike.numismaticgs.components.scoreboard.GoldSinkComponent;
import net.minecraft.util.Identifier;

public class NGSComponents implements ScoreboardComponentInitializer {
    public NGSComponents() {}

    private static Identifier id(String path) {
        return new Identifier(NGSServer.MOD_ID, path);
    }


    /* -------------------------------------------------------
     * Scoreboard-scoped Components (server-wide/stateful)
     * ----------------------------------------------------- */

    public static final ComponentKey<GoldSinkComponent> GOLD_SINK =
            ComponentRegistryV3.INSTANCE.getOrCreate(id("gold_sink"), GoldSinkComponent.class);


    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry scoreboardComponentFactoryRegistry) {
        scoreboardComponentFactoryRegistry.registerScoreboardComponent(GOLD_SINK, GoldSinkComponent::new);
    }
}
