package dev.quarris.fireandflames.data.tool.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.*;

public class ToolParts {

    public static final ToolParts EMPTY = new ToolParts("", Collections.emptyMap());

    public static final Codec<ToolParts> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("main_part_name").forGetter(toolParts -> toolParts.mainPartName),
        Codec.unboundedMap(
            Codec.STRING,
            ToolPart.CODEC
        ).fieldOf("parts").forGetter(parts -> parts.namedParts)
    ).apply(instance, ToolParts::new));

    private final String mainPartName;
    private final Map<String, ToolPart> namedParts;

    private ToolParts(String mainPartName, Map<String, ToolPart> namedParts) {
        this.mainPartName = mainPartName;
        this.namedParts = namedParts;
    }

    public ToolPart getMainPart() {
        return this.getPart(this.mainPartName);
    }

    public Set<String> partKeys() {
        return this.namedParts.keySet();
    }

    public ToolPart getPart(String name) {
        return this.namedParts.get(name);
    }

    public Collection<ToolPart> getAllParts() {
        return this.namedParts.values();
    }

    public boolean isEmpty() {
        return this == EMPTY || this.namedParts.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ToolParts toolParts = (ToolParts) o;
        return Objects.equals(mainPartName, toolParts.mainPartName) && Objects.equals(namedParts, toolParts.namedParts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainPartName, namedParts);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Map<String, ToolPart> namedParts = new Object2ObjectArrayMap<>();

        private Builder() {
        }

        public Builder add(String name, ToolPart toolPart) {
            this.namedParts.put(name, toolPart);
            return this;
        }

        public ToolParts build(String mainPartName) {
            return new ToolParts(mainPartName, this.namedParts);
        }

    }
}
