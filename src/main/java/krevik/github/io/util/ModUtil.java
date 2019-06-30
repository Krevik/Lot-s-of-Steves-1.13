package krevik.github.io.util;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ModUtil {
    @Nonnull
    public static <T> T _null() {
        return null;
    }
    public static <T extends IForgeRegistryEntry<T>> List<T> getModEntries(final IForgeRegistry<T> registry) {
        return registry.getValues().stream()
                .filter(entry -> Objects.requireNonNull(entry.getRegistryName()).getNamespace().equals(ModReference.MOD_ID))
                .collect(Collectors.toList());
    }

}
