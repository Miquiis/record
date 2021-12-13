package me.miquiis.record.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.miquiis.record.Record;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class RecordCommand {
    public RecordCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("record").then(Commands.literal("start").then(Commands.argument("name", StringArgumentType.string()).executes(context -> {
            String input = StringArgumentType.getString(context, "name");
            context.getSource().sendFeedback(new StringTextComponent("Started recording for " + input), true);
            Record.getInstance().getRecordManager().startRecording(context.getSource().asPlayer().getUniqueID(), input);
            return 1;
        }))).then(Commands.literal("stop").executes(context -> {
            context.getSource().sendFeedback(new StringTextComponent("Recording stopped."), true);
            Record.getInstance().getRecordManager().stopRecording(context.getSource().asPlayer().getUniqueID());
            return 1;
        })).then(Commands.literal("play").then(Commands.argument("name", StringArgumentType.string()).then(Commands.argument("entity", EntitySummonArgument.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).then(Commands.argument("nbt", NBTCompoundTagArgument.nbt()).executes(context -> {
            ResourceLocation entity = EntitySummonArgument.getEntityId(context, "entity");
            return Record.getInstance().getRecordManager().startPlaying(context.getSource().asPlayer(), StringArgumentType.getString(context, "name"), EntityType.byKey(entity.toString()).orElse(null), NBTCompoundTagArgument.getNbt(context, "nbt"));
        })))))
        );
    }

}
