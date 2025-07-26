package dev.quarris.fireandflames.datagen.client.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.quarris.fireandflames.client.model.ToolModel;
import dev.quarris.fireandflames.client.model.ToolModelLoader;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;

public class ToolModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

    public static <T extends ModelBuilder<T>> ToolModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new ToolModelBuilder<>(parent, existingFileHelper);
    }

    public List<ToolModel.PartSprite> parts = new ArrayList<>();

    private ToolModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(ToolModelLoader.ID, parent, existingFileHelper, false);
    }

    public ToolModelBuilder<T> part(ResourceLocation texture, int index) {
        this.parts.add(new ToolModel.PartSprite(texture, index));
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        super.toJson(json);
        ToolModel model = new ToolModel(this.parts);
        return ToolModel.CODEC.encode(model, JsonOps.INSTANCE, json).map(JsonElement::getAsJsonObject).getOrThrow();
    }
}
