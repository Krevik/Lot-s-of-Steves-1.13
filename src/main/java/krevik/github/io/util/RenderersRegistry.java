package krevik.github.io.util;

import krevik.github.io.client.render.RenderAutoFarmer;
import krevik.github.io.entity.EntityAutoFarmer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderersRegistry {
    public static void registerRenders() {
        RenderingRegistry.registerEntityRenderingHandler(EntityAutoFarmer.class, new RenderAutoFarmer.Factory());
    }
}
