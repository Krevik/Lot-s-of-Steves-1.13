package krevik.github.io.init;

import krevik.github.io.util.ModReference;
import krevik.github.io.util.ModUtil;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(ModReference.MOD_ID)
public class ModEntities {
    public static EntityType<?> ENTITY_AUTO_FARMER = ModUtil._null();
    public static EntityType<?> ENTITY_AUTO_LUMBERJACK = ModUtil._null();
}
