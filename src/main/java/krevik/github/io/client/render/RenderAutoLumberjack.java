package krevik.github.io.client.render;

import krevik.github.io.entity.EntityAutoLumberjack;
import krevik.github.io.util.TextureLocationsRef;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@OnlyIn(Dist.CLIENT)
public class RenderAutoLumberjack extends BipedRenderer<EntityAutoLumberjack, BipedModel<EntityAutoLumberjack>>
{

    public RenderAutoLumberjack(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new BipedModel<EntityAutoLumberjack>(1.0f), 0.8F);
    }


    @Override
    protected ResourceLocation getEntityTexture(EntityAutoLumberjack entity)
    {
        return TextureLocationsRef.AUTOLUMBERJACKLOC;
    }

    public static class Factory implements IRenderFactory<EntityAutoLumberjack> {

        @Override
        public EntityRenderer<? super EntityAutoLumberjack> createRenderFor(EntityRendererManager manager) {
            return new RenderAutoLumberjack(manager);
        }

    }

    @Override
    protected void applyRotations(EntityAutoLumberjack entityLiving, float p_77043_2_, float rotationYaw, float partialTicks)
    {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);

    }

}
