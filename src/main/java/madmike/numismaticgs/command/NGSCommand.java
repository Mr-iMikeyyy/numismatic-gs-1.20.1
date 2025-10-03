package madmike.numismaticgs.command;

import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.currency.CurrencyComponent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import madmike.numismaticgs.NGSComponents;
import madmike.numismaticgs.components.scoreboard.PlayerLevelsComponent;
import madmike.numismaticgs.components.scoreboard.PlayerNamesComponent;
import madmike.numismaticgs.util.CurrencyUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;

public class NGSCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            LiteralArgumentBuilder<ServerCommandSource> levelCommand = literal("level").executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayer();
                if (player != null) {
                    player.sendMessage(Text.literal("""
                            §6====== Level Command Help ======
                            
                            Level up at the cost of gold
                            Your Current Level + 1 = Gold needed for Level Up
                            
                            §e/level up §7- Upgrade your level if you have enough coins
                            §e/level see §7- View your current level
                            §e/level top §7- See Level Leader Board
                            """), false);
                }
                return 1;
            });

            levelCommand.then(literal("up").executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayer();
                if (player == null) {
                    ctx.getSource().sendError(Text.literal("You must be a player to use this command"));
                    return 0;
                }

                UUID playerId = player.getUuid();

                PlayerLevelsComponent gs = NGSComponents.PLAYER_LEVELS.get(ctx.getSource().getServer().getScoreboard());

                int level = gs.get(playerId);
                long cost = (level * 10000L) + 10000L;

                CurrencyComponent cc = ModComponents.CURRENCY.get(player);
                long wallet = cc.getValue();

                if (cost > wallet) {
                    player.sendMessage(Text.literal("You don't have enough gold, You need " + CurrencyUtil.fromTotalBronze(cost).gold() + " gold."));
                    return 0;
                }

                cc.modify(-cost);
                gs.increment(playerId, 1);

                player.sendMessage(Text.literal("You Leveled Up! Your level is now" + gs.get(playerId)));

                return 1;
            }));

            levelCommand.then(literal("see").executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayer();
                if (player == null) {
                    ctx.getSource().sendError(Text.literal("You must be a player to use this command"));
                    return 0;
                }

                int level = NGSComponents.PLAYER_LEVELS.get(player.getScoreboard()).get(player.getUuid());

                player.sendMessage(Text.literal("Your current level is: " + level));
                return 1;
            }));

            levelCommand.then(literal("top").executes(ctx -> {
                Scoreboard sb = ctx.getSource().getServer().getScoreboard();

                PlayerLevelsComponent plc = NGSComponents.PLAYER_LEVELS.get(sb);

                Map<UUID, Integer> levels = plc.getAll();

                // sort descending by level and take top 10
                List<Map.Entry<UUID, Integer>> top = levels.entrySet().stream()
                        .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                        .limit(10)
                        .toList();

                if (top.isEmpty()) {
                    ctx.getSource().sendFeedback(() -> Text.literal("No levels found."), false);
                    return 1;
                }

                MutableText msg = Text.literal("=== Top 10 Party Levels ===\n").formatted(Formatting.GOLD);

                PlayerNamesComponent pnc = NGSComponents.PLAYER_NAMES.get(sb);
                int rank = 1;
                for (Map.Entry<UUID, Integer> entry : top) {
                    UUID playerId = entry.getKey();
                    int level = entry.getValue();

                    String name = pnc.getName(playerId);

                    msg.append(Text.literal(rank + ". " + name + " - Level " + level + "\n")
                            .formatted(Formatting.YELLOW));

                    rank++;
                }
                ctx.getSource().sendFeedback(() -> msg, false);

                return 1;
            }));

            commandDispatcher.register(levelCommand);
        });
    }
}
