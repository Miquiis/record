package me.miquiis.record;

import me.miquiis.record.client.ClientKeybinds;
import me.miquiis.record.common.managers.FileManager;
import me.miquiis.record.common.managers.RecordManager;
import me.miquiis.record.common.models.RecordScript;
import me.miquiis.record.common.models.RecordTape;
import me.miquiis.record.common.models.RecordTick;
import me.miquiis.record.common.models.RecordTickEvent;
import me.miquiis.record.common.utils.JsonDeserializerWithInheritance;
import me.miquiis.record.server.managers.CommandManager;
import me.miquiis.record.server.network.RecordNetwork;
import net.minecraft.client.Minecraft;
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

    private CommandManager commandManager;

    private FileManager pathfindingFolder;
    private RecordManager recordManager;

    public Record() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        this.commandManager = new CommandManager();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(commandManager);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        instance = this;
        RecordNetwork.init();
        recordManager = new RecordManager(this);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        ClientKeybinds.registerBindings();

        pathfindingFolder = new FileManager("pathfinding", event.getMinecraftSupplier().get().gameDir,
                new FileManager.JsonDeserializer(RecordTickEvent.class, new JsonDeserializerWithInheritance<RecordTickEvent>())
        );
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
        pathfindingFolder = new FileManager("pathfinding", event.getServer().getDataDirectory(),
                new FileManager.JsonDeserializer(RecordTickEvent.class, new JsonDeserializerWithInheritance<RecordTickEvent>())
        );
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
