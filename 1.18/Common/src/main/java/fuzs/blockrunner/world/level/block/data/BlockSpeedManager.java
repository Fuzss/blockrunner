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
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BlockSpeedManager implements PreparableReloadListener {
    public static final BlockSpeedManager INSTANCE = new BlockSpeedManager();
    public static final UUID SPEED_MODIFIER_BLOCK_SPEED_UUID = UUID.fromString("23237052-61AD-11EB-AE93-0242AC130002");
    private static final String CONFIG_FILE_NAME = BlockRunner.MOD_ID + ".json";
    private static final Set<SpeedHolderValue> DEFAULT_BLOCK_SPEEDS = new SpeedHolderValue.Builder()
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
    public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_10780_, ResourceManager p_10781_, ProfilerFiller p_10782_, ProfilerFiller p_10783_, Executor p_10784_, Executor p_10785_) {
        return p_10780_.wait(Unit.INSTANCE).thenRunAsync(this::load, p_10785_);
    }

    public void load() {
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
        for (SpeedHolderValue value : values) {
            value.serialize(jsonElements);
        }
        return jsonElements;
    }

    private void deserialize(FileReader reader) {
        this.deserialize(JsonConfigFileUtil.GSON.fromJson(reader, JsonObject.class));
    }

    public void deserialize(JsonObject jsonElements) {
        this.blockSpeedValues.clear();
        this.blockSpeeds = null;
        Set<SpeedHolderValue> blockSpeedValues = Sets.newHashSet();
        for (Map.Entry<String, JsonElement> entry : jsonElements.entrySet()) {
            String key = entry.getKey();
            double speedValue = entry.getValue().getAsDouble();
            if (key.startsWith("#")) {
                TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(key.substring(1)));
                blockSpeedValues.add(new SpeedHolderValue.TagValue(tag, speedValue));
            } else {
                ResourceLocation resourcelocation = new ResourceLocation(key);
                if (Registry.BLOCK.containsKey(resourcelocation)) {
                    Block block = Registry.BLOCK.get(resourcelocation);
                    blockSpeedValues.add(new SpeedHolderValue.BlockValue(block, speedValue));
                } else {
                    BlockRunner.LOGGER.warn("Unknown block type '{}', valid types are: {}", resourcelocation, Joiner.on(", ").join(Registry.BLOCK.keySet()));
                }
            }
        }
        this.blockSpeedValues.addAll(blockSpeedValues);
    }
}
