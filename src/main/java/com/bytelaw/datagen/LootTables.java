package com.bytelaw.datagen;

import com.bytelaw.common.registry.RegistryList;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class LootTables implements IDataProvider {
    public static final Function<DataGenerator, LootTables> INSTANCE = LootTables::new;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Map<Block, LootTable.Builder> lootTables = Maps.newHashMap();
    private final DataGenerator generator;

    private LootTables(DataGenerator dataGeneratorIn) {
        this.generator = dataGeneratorIn;
    }

    @Override
    public void act(DirectoryCache cache) {
        addTables();
        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        for(Map.Entry<Block, LootTable.Builder> entry : lootTables.entrySet()) {
            tables.put(entry.getKey().getLootTable(), entry.getValue().setParameterSet(LootParameterSets.BLOCK).build());
        }
        Path outputFolder = generator.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                IDataProvider.save(GSON, cache, LootTableManager.toJson(lootTable), path);
            } catch(IOException e) {
                LOGGER.error("Couldn't write loot table {}", path, e);
            }
        });
    }

    private void addTables() {
        add(RegistryList.coloring_table);
    }

    private void add(Block block) {
        lootTables.put(block, createTable(block.getRegistryName().getPath(), block));
    }

    private LootTable.Builder createTable(String name, Block block) {
        LootPool.Builder builder = LootPool.builder()
                .name(name)
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(block))
                .acceptCondition(SurvivesExplosion.builder());
        return LootTable.builder().addLootPool(builder);
    }

    @Override
    public String getName() {
        return "LootTables";
    }
}
