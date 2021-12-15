package me.miquiis.record;

import me.miquiis.record.client.ClientKeybinds;
import me.miquiis.record.common.managers.FileManager;
import me.miquiis.record.common.managers.RecordManager;
import me.miquiis.record.common.models.RecordScript;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
        instance = this;

        recordManager = new RecordManager(this);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        ClientKeybinds.registerBindings();

        pathfindingFolder = new FileManager("pathfinding", event.getMinecraftSupplier().get().gameDir, RecordScript.RecordTick.class);
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
        pathfindingFolder = new FileManager("pathfinding", event.getServer().getDataDirectory(), RecordScript.RecordTick.class);
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
