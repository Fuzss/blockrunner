package fuzs.blockrunner.world.level.block.data;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fuzs.blockrunner.BlockRunner;
import fuzs.blockrunner.init.ModRegistry;
import fuzs.blockrunner.network.S2CBlockSpeedMessage;
import fuzs.puzzleslib.api.config.v3.json.JsonConfigFileUtil;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.Proxy;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BlockSpeedManager implements ResourceManagerReloadListener {
    public static final BlockSpeedManager INSTANCE = new BlockSpeedManager();
    public static final String SCHEMA_VERSION = String.valueOf(2);
    public static final UUID SPEED_MODIFIER_BLOCK_SPEED_UUID = UUID.fromString("23237052-61AD-11EB-AE93-0242AC130002");
    private static final String CONFIG_FILE_NAME = BlockRunner.MOD_ID + ".json";
    private static final Set<SpeedHolderValue> DEFAULT_BLOCK_SPEEDS = new SpeedHolderValue.Builder()
            .add(BlockTags.STONE_BRICKS, 1.15)
            .add(Blocks.DIRT_PATH, 1.35)
            .add(ModRegistry.VERY_SLOW_BLOCKS_BLOCK_TAG, 0.45)
            .add(ModRegistry.SLOW_BLOCKS_BLOCK_TAG, 0.65)
            .add(ModRegistry.SLIGHTLY_SLOW_BLOCKS_BLOCK_TAG, 0.85)
            .add(ModRegistry.SLIGHTLY_QUICK_BLOCKS_BLOCK_TAG, 1.15)
            .add(ModRegistry.QUICK_BLOCKS_BLOCK_TAG, 1.35)
            .add(ModRegistry.VERY_QUICK_BLOCKS_BLOCK_TAG, 1.55)
            .build();

    private final Set<SpeedHolderValue> blockSpeedValues = Sets.newHashSet();
    private Map<Block, Double> blockSpeeds;

    public void onPlayerLoggedIn(Player player) {
        if (ModLoaderEnvironment.INSTANCE.isServer()) {
            BlockRunner.NETWORK.sendTo(new S2CBlockSpeedMessage(this.serialize(this.blockSpeedValues)), (ServerPlayer) player);
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        JsonConfigFileUtil.getAndLoad(CONFIG_FILE_NAME, this::serialize, this::deserialize);
        // this is also called when the server is starting and Forge has not set the game server object yet
        if (ModLoaderEnvironment.INSTANCE.isServer() && Proxy.INSTANCE.getGameServer() != null) {
            BlockRunner.NETWORK.sendToAll(new S2CBlockSpeedMessage(this.serialize(this.blockSpeedValues)));
        }
    }

    private void dissolve() {
        if (this.blockSpeeds == null) {
            Map<Block, Double> blockSpeeds = Maps.newHashMap();
            try {
                for (SpeedHolderValue value : this.blockSpeedValues) {
                    value.addValues(blockSpeeds);
                }
            } catch (Exception e) {
                BlockRunner.LOGGER.error(e.getMessage());
                blockSpeeds.clear();
            }
            this.blockSpeeds = blockSpeeds;
        }
    }

    public boolean hasBlockSpeed(Block block) {
        this.dissolve();
        return this.blockSpeeds.containsKey(block);
    }

    public double getSpeedFactor(Block block) {
        this.dissolve();
        return this.blockSpeeds.getOrDefault(block, 1.0);
    }

    private void serialize(File jsonFile) {
        JsonConfigFileUtil.saveToFile(jsonFile, this.serialize(DEFAULT_BLOCK_SPEEDS));
    }

    private JsonObject serialize(Set<SpeedHolderValue> values) {
        JsonObject jsonElements = new JsonObject();
        jsonElements.addProperty("schemaVersion", SCHEMA_VERSION);
        for (SpeedHolderValue value : values) {
            value.serialize(jsonElements);
        }
        return jsonElements;
    }

    private void deserialize(FileReader reader) {
        this.deserialize(JsonConfigFileUtil.GSON.fromJson(reader, JsonObject.class));
    }

    public void deserialize(JsonObject jsonObject) {
        this.blockSpeedValues.clear();
        this.blockSpeeds = null;
        Map<Object, SpeedHolderValue> blockSpeedValues = Maps.newIdentityHashMap();
        String schemaVersion = GsonHelper.getAsString(jsonObject, "schema_version", "1");
        if (!schemaVersion.equals(SCHEMA_VERSION)) {
            BlockRunner.LOGGER.warn("Outdated config schema! Config might not work correctly. Current schema is {}.", SCHEMA_VERSION);
            blockSpeedValues.put(BlockTags.STONE_BRICKS, new SpeedHolderValue.TagValue(BlockTags.STONE_BRICKS, 1.15));
            blockSpeedValues.put(Blocks.DIRT_PATH, new SpeedHolderValue.BlockValue(Blocks.DIRT_PATH, 1.35));
        }
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            double speedValue = entry.getValue().getAsDouble();
            if (key.startsWith("#")) {
                TagKey<Block> tag = TagKey.create(Registries.BLOCK, new ResourceLocation(key.substring(1)));
                blockSpeedValues.put(tag, new SpeedHolderValue.TagValue(tag, speedValue));
            } else {
                ResourceLocation resourcelocation = new ResourceLocation(key);
                if (BuiltInRegistries.BLOCK.containsKey(resourcelocation)) {
                    Block block = BuiltInRegistries.BLOCK.get(resourcelocation);
                    blockSpeedValues.put(block, new SpeedHolderValue.BlockValue(block, speedValue));
                } else {
                    BlockRunner.LOGGER.warn("Unknown block type '{}', valid types are: {}", resourcelocation, Joiner.on(", ").join(BuiltInRegistries.BLOCK.keySet()));
                }
            }
        }
        this.blockSpeedValues.addAll(blockSpeedValues.values());
    }
}
