package me.miquiis.record.server.commands;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.miquiis.record.Record;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

public class CustomSuggestionProviders {

    public static final SuggestionProvider<CommandSource> AVAILABLE_TAPES = SuggestionProviders.register(new ResourceLocation("available_tapes"),
            (p, p2) -> ISuggestionProvider.suggest(Record.getInstance().getRecordManager().peekTapes(), p2)
    );


}
