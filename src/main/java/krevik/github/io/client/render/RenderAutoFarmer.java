package krevik.github.io.client.render;

import com.mojang.authlib.GameProfile;
import krevik.github.io.client.model.AutoFarmerModel;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.TextureLocationsRef;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.particle.BreakingParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5HeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.ParrotVariantLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class RenderAutoFarmer extends BipedRenderer<EntityAutoFarmer, AutoFarmerModel<EntityAutoFarmer>> {

    ItemRenderer itemRenderer;
    ItemStack stackToRender;
    ItemEntity itemEntity;
    public RenderAutoFarmer(EntityRendererManager renderManager) {
        super(renderManager, new AutoFarmerModel(1F,false), 1F);
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }


    @Override
    public void doRender(EntityAutoFarmer entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        renderLivingLabel(entity,x,y,z,10);
        stackToRender = entity.getItemStackToRender(entity.getMode());
        itemEntity = new ItemEntity(entity.world,0,999,0,stackToRender);
        renderItem(itemEntity,x,y+2.5f,z,entityYaw,partialTicks);
    }

    protected void renderLivingLabel(EntityAutoFarmer entityIn, double x, double y, double z, int maxDistance) {
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

    @Override
    public ResourceLocation getEntityTexture(EntityAutoFarmer entity) {
        return TextureLocationsRef.AUTOFARMERLOC;
    }

    public static class Factory implements IRenderFactory {

        @Override
        public EntityRenderer<? super EntityAutoFarmer> createRenderFor(EntityRendererManager manager) {
            return new RenderAutoFarmer(manager);
        }

    }


    float scale = 1f;
    public void renderItem(ItemEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
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

    protected ResourceLocation getEntityTexture(ItemEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }


}
