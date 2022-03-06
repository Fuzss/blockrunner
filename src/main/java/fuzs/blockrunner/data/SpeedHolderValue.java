package fuzs.blockrunner.data;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public abstract class SpeedHolderValue {
    final double speedMultiplier;

    public SpeedHolderValue(double speedMultiplier) {
        this.speedMultiplier = Mth.clamp(speedMultiplier, 0.0, 8.0);
    }

    public abstract void addValues(Map<Block, Double> blocks);

    abstract void serialize(JsonArray jsonArray);

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
        void serialize(JsonArray jsonArray) {
            JsonObject element = new JsonObject();
            element.addProperty(ForgeRegistries.BLOCKS.getKey(this.block).toString(), this.speedMultiplier);
            jsonArray.add(element);
        }
    }

    public static class TagValue extends SpeedHolderValue {
        private final TagKey<Block> tag;

        public TagValue(TagKey<Block> tag, double speed) {
            super(speed);
            this.tag = tag;
        }

        @Override
        public void addValues(Map<Block, Double> blocks) {
            for(Holder<Block> holder : Registry.BLOCK.getTagOrEmpty(this.tag)) {
                blocks.put(holder.value(), this.speedMultiplier);
            }
        }

        @Override
        void serialize(JsonArray jsonArray) {
            JsonObject element = new JsonObject();
            element.addProperty("#" + this.tag.location().toString(), this.speedMultiplier);
            jsonArray.add(element);
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

        public Set<SpeedHolderValue> get() {
            return ImmutableSet.copyOf(this.entries);
        }
    }

    public static class Serializer implements JsonDeserializer<SpeedHolderValue>, JsonSerializer<SpeedHolderValue> {
        @Override
        public SpeedHolderValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = JSONUtils.getString(jsonObject, "type");
            double speedFactor = JSONUtils.getFloat(jsonObject, "factor");
            if (type.startsWith("#")) {
                ResourceLocation location = new ResourceLocation(type.substring(1));
                return new TagValue(TagCollectionManager.getManager().getBlockTags().getTagByID(location), speedFactor);
            } else {
                ResourceLocation resourcelocation = new ResourceLocation(type);
                if (ForgeRegistries.BLOCKS.containsKey(resourcelocation)) {
                    return new BlockValue(ForgeRegistries.BLOCKS.getValue(resourcelocation), speedFactor);
                }
                throw new JsonSyntaxException("Unknown block type '" + resourcelocation + "', valid types are: " + Joiner.on(", ").join(ForgeRegistries.BLOCKS.getKeys()));
            }
        }

        @Override
        public JsonElement serialize(SpeedHolderValue src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.add("type", src.serialize());
            jsonobject.addProperty("factor", src.speedMultiplier);
            return jsonobject;
        }
    }
}
