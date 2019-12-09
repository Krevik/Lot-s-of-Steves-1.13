package krevik.github.io.init;

import krevik.github.io.entity.*;
import krevik.github.io.util.ModReference;
import krevik.github.io.util.ModUtil;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(ModReference.MOD_ID)
public class ModEntities {
    public static final EntityType<EntityAutoFarmer> AUTO_FARMER = ModUtil._null();
    public static final EntityType<EntityAutoLumberjack> AUTO_LUMBERJACK = ModUtil._null();
    public static final EntityType<EntityAutoMiner> AUTO_MINER = ModUtil._null();
}
