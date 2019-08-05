package krevik.github.io.client.render;

import krevik.github.io.client.model.AutoFarmerModel;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.TextureLocationsRef;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderAutoFarmer extends BipedRenderer<EntityAutoFarmer, AutoFarmerModel<EntityAutoFarmer>> {

    public RenderAutoFarmer(EntityRendererManager renderManager) {
        super(renderManager, new AutoFarmerModel(1F,false), 1F);
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


}
