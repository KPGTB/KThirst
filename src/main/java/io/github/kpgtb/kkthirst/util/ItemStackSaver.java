/*
 * Copyright 2022 KPG-TB
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.kpgtb.kkthirst.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.Arrays;

public class ItemStackSaver {
    public String save(ItemStack itemStack) throws IOException {
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

    public ItemStack load(String fileContent) throws IOException {
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
