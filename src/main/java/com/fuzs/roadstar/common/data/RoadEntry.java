package com.fuzs.roadstar.common.data;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;

public abstract class RoadEntry {

    private final double factor;

    public RoadEntry(double factor) {

        this.factor = MathHelper.clamp(factor, 0.0, 8.0);
    }

    protected abstract JsonElement getJsonType();

    public abstract boolean test(Block block);

    public double getSpeedFactor() {

        return this.factor;
    }

    public static class BlockEntry extends RoadEntry {

        private final Block block;

        public BlockEntry(Block block, double speed) {

            super(speed);
            this.block = block;
        }

        @Override
        public boolean test(Block block) {

            return this.block == block;
        }

        @Override
        protected JsonElement getJsonType() {
            
            return new JsonPrimitive(Objects.requireNonNull(this.block.getRegistryName()).toString());
        }
        
    }

    public static class TagEntry extends RoadEntry {

        private final ITag<Block> tag;

        public TagEntry(ITag<Block> tag, double speed) {

            super(speed);
            this.tag = tag;
        }

        @Override
        public boolean test(Block block) {

            return this.tag.contains(block);
        }

        @Override
        protected JsonElement getJsonType() {

            return new JsonPrimitive("#" + TagCollectionManager.getManager().getBlockTags().getValidatedIdFromTag(this.tag));
        }
        
    }
    
    public static class Builder {
        
        private final Set<RoadEntry> entries = Sets.newHashSet();
        
        public Builder add(Block block, double speedFactor) {
            
            this.entries.add(new BlockEntry(block, speedFactor));
            return this;
        }
        
        public Builder add(ITag<Block> tag, double speedFactor) {

            this.entries.add(new TagEntry(tag, speedFactor));
            return this;
        }

        public Set<RoadEntry> get() {

            return ImmutableSet.copyOf(this.entries);
        }
        
    }

    public static class Serializer implements JsonDeserializer<RoadEntry>, JsonSerializer<RoadEntry> {

        @Override
        public RoadEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            
            JsonObject jsonObject = json.getAsJsonObject();
            String type = JSONUtils.getString(jsonObject, "type");
            double speedFactor = JSONUtils.getFloat(jsonObject, "factor");
            if (type.startsWith("#")) {
                
                ResourceLocation location = new ResourceLocation(type.substring(1));
                return new TagEntry(TagCollectionManager.getManager().getBlockTags().getTagByID(location), speedFactor);
            } else {
                
                ResourceLocation resourcelocation = new ResourceLocation(type);
                if (ForgeRegistries.BLOCKS.containsKey(resourcelocation)) {
                    
                    return new BlockEntry(ForgeRegistries.BLOCKS.getValue(resourcelocation), speedFactor);
                }
                
                throw new JsonSyntaxException("Unknown block type '" + resourcelocation + "', valid types are: " + Joiner.on(", ").join(ForgeRegistries.BLOCKS.getKeys()));
            }
        }

        @Override
        public JsonElement serialize(RoadEntry src, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject jsonobject = new JsonObject();
            jsonobject.add("type", src.getJsonType());
            jsonobject.addProperty("factor", src.factor);
            
            return jsonobject;
        }
        
    }

}
