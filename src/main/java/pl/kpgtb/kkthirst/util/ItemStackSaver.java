package pl.kpgtb.kkthirst.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.Arrays;

public class ItemStackSaver {
    public static String save(ItemStack itemStack) throws IOException {
        File file = new File("temp.yml");
        file.createNewFile();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.set("item", itemStack);
        yamlConfiguration.save(file);

        StringBuilder result = new StringBuilder();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        reader.lines().forEach(line -> result.append(line).append("\n"));
        reader.close();
        file.delete();
        return result.toString();
    }

    public static ItemStack load(String fileContent) throws IOException {
        File file =  new File("temp.yml");

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String[] lines = fileContent.split("\n");

        Arrays.stream(lines).forEach(line -> {
            try {
                writer.write(line);
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        writer.close();

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        ItemStack result = yamlConfiguration.getItemStack("item");
        file.delete();
        return result;
    }
}