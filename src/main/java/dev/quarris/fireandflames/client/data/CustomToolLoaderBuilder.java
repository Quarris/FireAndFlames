package dev.quarris.fireandflames.client.data;

import com.google.gson.JsonObject;
import dev.quarris.fireandflames.client.model.CustomToolModelLoader;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class CustomToolLoaderBuilder extends CustomLoaderBuilder<ItemModelBuilder> {

    public CustomToolLoaderBuilder(ItemModelBuilder parent, ExistingFileHelper existingFileHelper) {
        super(CustomToolModelLoader.ID, parent, existingFileHelper, false);
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        return super.toJson(json);
    }
}
