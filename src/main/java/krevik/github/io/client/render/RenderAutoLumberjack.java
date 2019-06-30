package krevik.github.io.client.render;

import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.entity.EntityAutoLumberjack;
import krevik.github.io.util.TextureLocationsRef;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.model.ModelPlayer;
import net.minecraft.client.renderer.entity.model.ModelZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@OnlyIn(Dist.CLIENT)
public class RenderAutoLumberjack extends RenderBiped<EntityAutoLumberjack>
{

    public RenderAutoLumberjack(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelPlayer(1.0f,true), 0.8F);
    }


    @Override
    protected ResourceLocation getEntityTexture(EntityAutoLumberjack entity)
    {
        return TextureLocationsRef.AUTOLUMBERJACKLOC;
    }

    public static class Factory implements IRenderFactory<EntityAutoLumberjack> {

        @Override
        public Render<? super EntityAutoLumberjack> createRenderFor(RenderManager manager) {
            return new RenderAutoLumberjack(manager);
        }

    }

    @Override
    protected void applyRotations(EntityAutoLumberjack entityLiving, float p_77043_2_, float rotationYaw, float partialTicks)
    {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);

    }

}
