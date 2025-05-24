package dev.quarris.fireandflames.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.quarris.fireandflames.ModRef;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class CustomToolModelLoader implements IGeometryLoader<CustomToolModel> {

    public static final CustomToolModelLoader INSTANCE = new CustomToolModelLoader();
    public static final ResourceLocation ID = ModRef.res("custom_tool_loader");

    private CustomToolModelLoader() {}

    @Override
    public CustomToolModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        return new CustomToolModel();
    }
}
