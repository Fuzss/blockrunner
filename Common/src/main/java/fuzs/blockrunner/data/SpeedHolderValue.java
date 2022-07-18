package fuzs.blockrunner.data;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import fuzs.blockrunner.BlockRunner;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SpeedHolderValue {
    final double speedMultiplier;

    public SpeedHolderValue(double speedMultiplier) {
        this.speedMultiplier = Mth.clamp(speedMultiplier, 0.1, 8.0);
    }

    public abstract void addValues(Map<Block, Double> blocks) throws JsonSyntaxException;

    abstract void serialize(JsonObject jsonObject);

    public static class BlockValue extends SpeedHolderValue {
        private final Block block;

        public BlockValue(Block block, double speed) {
            super(speed);
            this.block = block;
        }

        @Override
        public void addValues(Map<Block, Double> blocks) {
            blocks.put(this.block, this.speedMultiplier);
        }

        @Override
        void serialize(JsonObject jsonObject) {
            jsonObject.addProperty(Registry.BLOCK.getKey(this.block).toString(), this.speedMultiplier);
        }
    }

    public static class TagValue extends SpeedHolderValue {
        private final TagKey<Block> tag;

        public TagValue(TagKey<Block> tag, double speed) {
            super(speed);
            this.tag = tag;
        }

        @Override
        public void addValues(Map<Block, Double> blocks) throws JsonSyntaxException {
            if (Registry.BLOCK.isKnownTagName(this.tag)) {
                for (Holder<Block> holder : Registry.BLOCK.getTagOrEmpty(this.tag)) {
                    blocks.putIfAbsent(holder.value(), this.speedMultiplier);
                }
            } else {
                String allowedLocations = Registry.BLOCK.getTagNames().map(TagKey::location).map(ResourceLocation::toString).collect(Collectors.joining(", "));
                BlockRunner.LOGGER.warn("Unknown block tag type '{}', valid types are: {}", this.tag.location(), allowedLocations);
            }
        }

        @Override
        void serialize(JsonObject jsonObject) {
            jsonObject.addProperty("#" + this.tag.location().toString(), this.speedMultiplier);
        }
    }
    
    public static class Builder {
        private final Set<SpeedHolderValue> entries = Sets.newHashSet();
        
        public Builder add(Block block, double speedFactor) {
            this.entries.add(new BlockValue(block, speedFactor));
            return this;
        }
        
        public Builder add(TagKey<Block> tag, double speedFactor) {
            this.entries.add(new TagValue(tag, speedFactor));
            return this;
        }

        public Set<SpeedHolderValue> build() {
            return ImmutableSet.copyOf(this.entries);
        }
    }
}
