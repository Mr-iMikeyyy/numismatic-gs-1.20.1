package madmike.numismaticgs.components.scoreboard;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerNamesComponent implements ComponentV3 {

    private final MinecraftServer sv;
    private final Scoreboard sb;

    public PlayerNamesComponent(Scoreboard sb, MinecraftServer sv) {
        this.sv = sv;
        this.sb = sb;
    }

    // last-known canonical usernames by UUID
    private final Map<UUID, String> playerNames = new HashMap<>();

    // --- API ---

    /** Returns the last-known canonical username or null if unknown. */
    public String getName(UUID playerId) {
        return playerNames.get(playerId);
    }

    /** Sets/updates the stored username for the player. */
    public void setName(UUID playerId, String username) {
        if (username == null) {
            playerNames.remove(playerId);
        } else {
            playerNames.put(playerId, username);
        }
    }

    /** Removes a stored name for the given player. */
    public void remove(UUID playerId) {
        playerNames.remove(playerId);
    }

    /** Defensive copy for iteration/debugging. */
    public Map<UUID, String> getAll() {
        return new HashMap<>(playerNames);
    }

    /** Convenience helper to record the name for a just-joined player. */
    public void onPlayerJoin(ServerPlayerEntity player) {
        UUID id = player.getUuid();
        String username = player.getGameProfile().getName(); // canonical username
        setName(id, username);
    }

    // --- Serialization ---

    @Override
    public void readFromNbt(NbtCompound tag) {
        playerNames.clear();
        if (tag.contains("PlayerNames", NbtElement.COMPOUND_TYPE)) {
            NbtCompound names = tag.getCompound("PlayerNames");
            for (String key : names.getKeys()) {
                try {
                    UUID id = UUID.fromString(key);
                    String name = names.getString(key);
                    if (!name.isEmpty()) {
                        playerNames.put(id, name);
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip bad UUID keys gracefully
                }
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtCompound names = new NbtCompound();
        for (Map.Entry<UUID, String> e : playerNames.entrySet()) {
            names.putString(e.getKey().toString(), e.getValue());
        }
        tag.put("PlayerNames", names);
    }
}
