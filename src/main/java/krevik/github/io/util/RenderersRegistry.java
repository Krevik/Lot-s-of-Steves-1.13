package krevik.github.io.util;

import krevik.github.io.client.render.RenderAutoFarmer;
import krevik.github.io.client.render.RenderAutoLumberjack;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.entity.EntityAutoLumberjack;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderersRegistry {
    public static void registerRenders() {
        RenderingRegistry.registerEntityRenderingHandler(EntityAutoFarmer.class, new RenderAutoFarmer.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityAutoLumberjack.class, new RenderAutoLumberjack.Factory());
    }
}
