package krevik.github.io.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import krevik.github.io.client.model.AutoLumberjackModel;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.entity.EntityAutoLumberjack;
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
public class RenderAutoLumberjack extends BipedRenderer<EntityAutoLumberjack, BipedModel<EntityAutoLumberjack>>
{
    ItemRenderer itemRenderer;
    ItemStack stackToRender;
    ItemEntity itemEntity;
    public RenderAutoLumberjack(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new AutoLumberjackModel<>(1.0f,false), 1F);
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void doRender(EntityAutoLumberjack entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        renderLivingLabel(entity,x,y,z,10);
        stackToRender = entity.getItemStackToRender(entity.getMode());
        itemEntity = new ItemEntity(entity.world,0,999,0,stackToRender);
        renderItem(x,y+2.5f,z);
    }

    protected void renderLivingLabel(EntityAutoLumberjack entityIn, double x, double y, double z, int maxDistance) {
        float f = this.renderManager.playerViewY;
        float f1 = this.renderManager.playerViewX;
        float f2 = entityIn.getHeight() + 0.5F;
        drawMode(this.getFontRendererFromRenderManager(), "Mode: " + entityIn.getMode(), (float)x, (float)y + f2, (float)z, 0, f, f1, false);
    }

    public static void drawMode(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isSneaking) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, z);
        GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scalef(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        if (!isSneaking) {
            GlStateManager.disableDepthTest();
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        int i = fontRendererIn.getStringWidth(str) / 2;
        GlStateManager.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        float f = Minecraft.getInstance().gameSettings.func_216840_a(0.25F);
        bufferbuilder.pos((double)(-i - 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, f).endVertex();
        bufferbuilder.pos((double)(-i - 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, f).endVertex();
        bufferbuilder.pos((double)(i + 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, f).endVertex();
        bufferbuilder.pos((double)(i + 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, f).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture();
        if (!isSneaking) {
            fontRendererIn.drawString(str, (float)(-fontRendererIn.getStringWidth(str) / 2), (float)verticalShift, 553648127);
            GlStateManager.enableDepthTest();
        }

        GlStateManager.depthMask(true);
        fontRendererIn.drawString(str, (float)(-fontRendererIn.getStringWidth(str) / 2), (float)verticalShift, isSneaking ? 553648127 : -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    float scale = 1f;
    public void renderItem(double x, double y, double z) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(this.scale, this.scale, this.scale);
        GlStateManager.rotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
        GlStateManager.enableNormalize();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.disableNormalize();
        this.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
        }

        this.itemRenderer.renderItem(stackToRender, ItemCameraTransforms.TransformType.GROUND);
        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
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
