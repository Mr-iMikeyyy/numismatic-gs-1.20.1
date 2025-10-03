package madmike.numismaticgs.components.scoreboard;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerLevelsComponent implements ComponentV3 {

    private MinecraftServer sv;
    private Scoreboard sb;

    public PlayerLevelsComponent(Scoreboard sb, MinecraftServer sv) {
        this.sv = sv;
        this.sb = sb;
    }

    private final Map<UUID, Integer> levels = new HashMap<>();

    // --- Basic API ---

    public void increment(UUID playerId, int amount) {
        levels.put(playerId, levels.getOrDefault(playerId, 0) + amount);
    }

    public int get(UUID playerId) {
        return levels.getOrDefault(playerId, 0);
    }

    public void delete(UUID playerId) {
        levels.remove(playerId);
    }

    public boolean has(UUID playerId) {
        return levels.containsKey(playerId);
    }

    public Map<UUID, Integer> getAll() {
        return new HashMap<>(levels); // return a copy for safety
    }

    // --- Serialization ---

    @Override
    public void readFromNbt(NbtCompound nbt) {
        levels.clear();

        if (nbt.contains("Levels", NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList("Levels", NbtElement.COMPOUND_TYPE);

            for (int i = 0; i < list.size(); i++) {
                NbtCompound entry = list.getCompound(i);
                if (entry.containsUuid("Player") && entry.contains("Value", NbtElement.INT_TYPE)) {
                    UUID playerId = entry.getUuid("Player");
                    int value = entry.getInt("Value");
                    levels.put(playerId, value);
                }
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        NbtList list = new NbtList();

        for (Map.Entry<UUID, Integer> entry : levels.entrySet()) {
            NbtCompound compound = new NbtCompound();
            compound.putUuid("Player", entry.getKey());
            compound.putInt("Value", entry.getValue());
            list.add(compound);
        }

        nbt.put("Levels", list);
    }
}
