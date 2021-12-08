package me.miquiis.record.common.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileManager {

    private Gson gson;

    private File minecraftFolder;
    private File mainFolder;
    private boolean isFirstTime;

    public FileManager(String filePath)
    {
        this.minecraftFolder = Minecraft.getInstance().gameDir;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.mainFolder = new File(minecraftFolder, filePath);
        this.isFirstTime = createFolder();
    }

    public FileManager(String filePath, RuntimeTypeAdapterFactory<?> adapterFactory)
    {
        this.minecraftFolder = Minecraft.getInstance().gameDir;
        this.gson = new GsonBuilder().registerTypeAdapterFactory(adapterFactory).setPrettyPrinting().create();
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
        try
        {
            final String json = gson.toJson(object);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        final String json = gson.toJson(object);

        File objectFile = new File(mainFolder, fileName + ".json");
        objectFile.delete();

        try {
            Files.write(objectFile.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            return true;
        } catch (IOException e) {
            System.out.println(e.toString());
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
            System.out.println(e.toString());
            e.printStackTrace();
        }

        return null;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }
}
