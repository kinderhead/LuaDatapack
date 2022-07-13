package mod.kinderhead.luadatapack;

import java.util.Locale;

import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.ValueFactory;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import mod.kinderhead.luadatapack.datapack.Scripts;
import mod.kinderhead.luadatapack.lua.LuaRunner;
import mod.kinderhead.luadatapack.lua.api.MCLuaFactory;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.text.Text;

public class LuaCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(
            CommandManager.literal("lua")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.argument("name", IdentifierArgumentType.identifier())
                .suggests((ctx, builder) -> {
                    CommandSource.forEachMatching(Scripts.idSet(true), builder.getRemaining().toLowerCase(Locale.ROOT), id -> id, id -> builder.suggest(id.toString()));
                    return builder.buildFuture();
                })
                .executes(ctx -> run(ctx, new String[0]))
                .then(CommandManager.argument("args", MessageArgumentType.message())
                    .executes(ctx -> run(ctx, MessageArgumentType.getMessage(ctx, "args").getString().strip().split(" ")))
                )
            )
        );
    }

    private static int run(CommandContext<ServerCommandSource> ctx, String[] args) {
        var id = IdentifierArgumentType.getIdentifier(ctx, "name");

        String code = Scripts.get(id);
        if (code == null) {
            ctx.getSource().sendFeedback(Text.translatable("text.luadatapack.file_not_found", id.toString()), false);
            return -1;
        }

        try {
            if (exec(code, id.toString(), ctx.getSource(), args) == -1) {
                ctx.getSource().sendFeedback(Text.translatable("text.luadatapack.lua_error", id.toString()), false);
                return -1;
            }
        } catch (LuaError e) {
            return -1;
        }
        
        return 1;
    }

    public static int exec(String code, String name, ServerCommandSource source, String[] args) throws LuaError {
        LuaTable env = new LuaTable();
        env.rawset("src", MCLuaFactory.get(source));

        LuaTable a = new LuaTable();
        for (String string : args) {
            a.insert(0, ValueFactory.valueOf(string));
        }
        env.rawset("args", a);

        if (LuaRunner.Run(code, name, env)) {
            return 1;
        }

        return -1;
    }
}
