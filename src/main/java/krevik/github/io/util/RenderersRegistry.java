package krevik.github.io.util;

import krevik.github.io.client.render.RenderAutoFarmer;
import krevik.github.io.init.ModEntities;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderersRegistry {
    public static void registerRenders() {
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.AUTO_FARMER, new RenderAutoFarmer.Factory());
    }
}
