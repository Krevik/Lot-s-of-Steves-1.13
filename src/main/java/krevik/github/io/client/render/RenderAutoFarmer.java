package krevik.github.io.client.render;

import krevik.github.io.entity.EntityAutoFarmer;
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
public class RenderAutoFarmer extends RenderBiped<EntityAutoFarmer>
{

    public RenderAutoFarmer(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelPlayer(1.0f,true), 0.8F);
    }


    @Override
    protected ResourceLocation getEntityTexture(EntityAutoFarmer entity)
    {
        return TextureLocationsRef.AUTOFARMERLOC;
    }

    public static class Factory implements IRenderFactory<EntityAutoFarmer> {

        @Override
        public Render<? super EntityAutoFarmer> createRenderFor(RenderManager manager) {
            return new RenderAutoFarmer(manager);
        }

    }

    @Override
    protected void applyRotations(EntityAutoFarmer entityLiving, float p_77043_2_, float rotationYaw, float partialTicks)
    {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);

    }

}
