package krevik.github.io.init;

import krevik.github.io.util.ModReference;
import krevik.github.io.util.ModUtil;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(ModReference.MOD_ID)
public class ModEntities {
    public static EntityType<?> ENTITY_AUTO_FARMER = ModUtil._null();
    public static EntityType<?> ENTITY_AUTO_LUMBERJACK = ModUtil._null();

    public static void registerPlacementType(EntityType<?> type,EntitySpawnPlacementRegistry.SpawnPlacementType spawnType){
        EntitySpawnPlacementRegistry.register(type, spawnType, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,null);
    }

    public static void registerPlacementTypes(){
        registerPlacementType(ENTITY_AUTO_FARMER, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND);
        registerPlacementType(ENTITY_AUTO_LUMBERJACK, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND);
    }
}
