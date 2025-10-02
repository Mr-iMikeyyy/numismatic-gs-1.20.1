package madmike.numismaticgs.command;

import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.currency.CurrencyComponent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import madmike.numismaticgs.NGSComponents;
import madmike.numismaticgs.components.scoreboard.GoldSinkComponent;
import madmike.numismaticgs.util.CurrencyUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;

public class NGSCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            LiteralArgumentBuilder<ServerCommandSource> levelUpCommand = literal("levelup").executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayer();
                if (player != null) {
                    player.sendMessage(Text.literal("""
                            §6====== Level Up Command Help ======
                            
                            Invest your coins in yourself and level yourself up
                            Your Current Level + 1 = Gold needed for Level Up
                            
                            §e/levelup confirm §7- Upgrade your level if you have enough coins
                            """), false);
                }
                return 1;
            });

            levelUpCommand.then(literal("confirm").executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayer();
                if (player == null) {
                    ctx.getSource().sendError(Text.literal("You must be a player to use this command"));
                    return 0;
                }

                UUID playerId = player.getUuid();

                GoldSinkComponent gs = NGSComponents.GOLD_SINK.get(ctx.getSource().getServer().getScoreboard());

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

            commandDispatcher.register(levelUpCommand);
        });
    }
}
