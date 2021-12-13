package me.miquiis.record;

import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import me.miquiis.record.client.ClientKeybinds;
import me.miquiis.record.common.managers.FileManager;
import me.miquiis.record.common.managers.RecordManager;
import me.miquiis.record.common.models.RecordScript;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Record.MOD_ID)
public class Record
{
    public static final String MOD_ID = "record";

    private static Record instance;

    // SERVER SIDE

    // CLIENT SIDE

    // COMMON SIDE

    private FileManager pathfindingFolder;
    private RecordManager recordManager;

    public Record() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        ClientKeybinds.registerBindings();
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        instance = this;

        pathfindingFolder = new FileManager("pathfinding",
                RuntimeTypeAdapterFactory.of(RecordScript.RecordTick.RecordTickEvent.class, "type")
                        .registerSubtype(RecordScript.RecordTick.ItemRecordTickEvent.class, "item")
        );

        recordManager = new RecordManager(this);
    }

    public RecordManager getRecordManager() {
        return recordManager;
    }

    public FileManager getPathfindingFolder() {
        return pathfindingFolder;
    }

    public static Record getInstance() {
        return instance;
    }
}
