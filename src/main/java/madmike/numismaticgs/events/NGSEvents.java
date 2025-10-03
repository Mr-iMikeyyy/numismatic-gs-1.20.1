package madmike.numismaticgs.events;

import madmike.numismaticgs.NGSComponents;
import madmike.numismaticgs.components.scoreboard.PlayerNamesComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class NGSEvents {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();

            // Example: scoreboard-scoped component; replace with your accessor
            var comp = NGSComponents.PLAYER_NAMES.get(minecraftServer.getScoreboard());

            // Record the canonical username on join
            comp.onPlayerJoin(player);
        });
    }
}
