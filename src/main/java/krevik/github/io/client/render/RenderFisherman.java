package krevik.github.io.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import krevik.github.io.client.model.AutoLumberjackModel;
import krevik.github.io.client.model.ModelFisherman;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.entity.EntityAutoLumberjack;
import krevik.github.io.entity.EntityFisherman;
import krevik.github.io.util.TextureLocationsRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@OnlyIn(Dist.CLIENT)
public class RenderFisherman extends BipedRenderer<EntityFisherman, ModelFisherman<EntityFisherman>>
{
    ItemRenderer itemRenderer;
    ItemStack stackToRender;
    ItemEntity itemEntity;
    public RenderFisherman(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new ModelFisherman<EntityFisherman>(1.0f,false), 1F);
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void doRender(EntityFisherman entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityFisherman entity)
    {
        return TextureLocationsRef.AUTOLUMBERJACKLOC;
    }

    public static class Factory implements IRenderFactory<EntityFisherman> {

        @Override
        public EntityRenderer<? super EntityFisherman> createRenderFor(EntityRendererManager manager) {
            return new RenderFisherman(manager);
        }

    }

    @Override
    protected void applyRotations(EntityFisherman entityLiving, float p_77043_2_, float rotationYaw, float partialTicks)
    {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);

    }

}
