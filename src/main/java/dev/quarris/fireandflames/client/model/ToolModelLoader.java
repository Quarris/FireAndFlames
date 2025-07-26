package dev.quarris.fireandflames.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import dev.quarris.fireandflames.ModRef;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class ToolModelLoader implements IGeometryLoader<ToolModel> {

    public static final ResourceLocation ID = ModRef.res("tool");
    public static final ToolModelLoader INSTANCE = new ToolModelLoader();

    private ToolModelLoader() {}

    @Override
    public ToolModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        DataResult<ToolModel> result = ToolModel.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, jsonObject));
        return result.resultOrPartial(ModRef.LOGGER::error).orElseThrow(() -> new JsonParseException("Could not load tool model."));
    }
}
