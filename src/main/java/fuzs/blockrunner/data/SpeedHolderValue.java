package fuzs.blockrunner.data;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

public abstract class SpeedHolderValue {
    final double speedMultiplier;

    public SpeedHolderValue(double speedMultiplier) {
        this.speedMultiplier = Mth.clamp(speedMultiplier, 0.0, 8.0);
    }

    public abstract void addValues(Map<Block, Double> blocks);

    abstract void serialize(JsonObject jsonObject);

    public static void addValue(Block block, double speed, Map<Block, Double> blocks) {
        new BlockValue(block, speed).addValues(blocks);
    }

    public static void addValue(TagKey<Block> tag, double speed, Map<Block, Double> blocks) {
        new TagValue(tag, speed).addValues(blocks);
    }

    public static class BlockValue extends SpeedHolderValue {
        private final Block block;

        private BlockValue(Block block, double speed) {
            super(speed);
            this.block = block;
        }

        @Override
        public void addValues(Map<Block, Double> blocks) {
            blocks.put(this.block, this.speedMultiplier);
        }

        @Override
        void serialize(JsonObject jsonObject) {
            jsonObject.addProperty(ForgeRegistries.BLOCKS.getKey(this.block).toString(), this.speedMultiplier);
        }
    }

    public static class TagValue extends SpeedHolderValue {
        private final TagKey<Block> tag;

        private TagValue(TagKey<Block> tag, double speed) {
            super(speed);
            this.tag = tag;
        }

        @Override
        public void addValues(Map<Block, Double> blocks) {
            for (Holder<Block> holder : Registry.BLOCK.getTagOrEmpty(this.tag)) {
                blocks.put(holder.value(), this.speedMultiplier);
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
