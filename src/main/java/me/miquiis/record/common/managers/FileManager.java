package me.miquiis.record.common.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import me.miquiis.record.common.models.RecordTickEvent;
import me.miquiis.record.common.utils.JsonDeserializerWithInheritance;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileManager {

    public static class JsonDeserializer {

        public Class<?> klass;
        public JsonDeserializerWithInheritance<?> deserializer;

        public<T> JsonDeserializer(Class<T> klass, JsonDeserializerWithInheritance<T> deserializer)
        {
            this.klass = klass;
            this.deserializer = deserializer;
        }

    }

    private Gson gson;
    private Gson deserializer;

    private File minecraftFolder;
    private File mainFolder;
    private boolean isFirstTime;

    public FileManager(String filePath)
    {
        this.minecraftFolder = Minecraft.getInstance().gameDir;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.deserializer = new GsonBuilder().setPrettyPrinting().create();
        this.mainFolder = new File(minecraftFolder, filePath);
        this.isFirstTime = createFolder();
    }

    public FileManager(String filePath, File directory, JsonDeserializer... jsonDeserializers)
    {
        this.minecraftFolder = directory;

        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();

        for (JsonDeserializer jsonDeserializer : jsonDeserializers)
        {
            gsonBuilder.registerTypeAdapter(jsonDeserializer.klass, jsonDeserializer.deserializer);
        }

        this.gson = gsonBuilder.create();
        this.deserializer = new GsonBuilder().setPrettyPrinting().create();
        this.mainFolder = new File(minecraftFolder, filePath);
        this.isFirstTime = createFolder();
    }

    private boolean createFolder()
    {
        if (!mainFolder.exists()) {
            return mainFolder.mkdir();
        }

        return false;
    }

    public boolean saveObject(String fileName, Object object)
    {
        final String json = deserializer.toJson(object);

        File objectFile = new File(mainFolder, fileName + ".json");
        objectFile.delete();

        try {
            Files.write(objectFile.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public <T> List<T> loadObjects(Class<T> objectClass)
    {
        final List<T> toReturn = new ArrayList<>();

        File[] files = mainFolder.listFiles();

        for (File file : files)
        {
            toReturn.add(loadObject(file.getName(), objectClass, true));
        }

        toReturn.removeIf(Objects::isNull);

        return toReturn;
    }

    public <T> T loadObject(String fileName, Class<T> objectClass, boolean hasExtension)
    {
        final File objectFile = new File(mainFolder, fileName + (!hasExtension ? ".json" : ""));

        if (!objectFile.exists()) return null;

        try {
            JsonReader jsonReader = new JsonReader(new FileReader(objectFile));
            T object = gson.fromJson(jsonReader, objectClass);
            jsonReader.close();
            return object;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }
}
