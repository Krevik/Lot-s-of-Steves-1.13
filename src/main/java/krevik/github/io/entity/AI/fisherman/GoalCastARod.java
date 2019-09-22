package krevik.github.io.entity.AI.fisherman;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityCustomFishingBobber;
import krevik.github.io.entity.EntityFisherman;
import krevik.github.io.util.FunctionHelper;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class GoalCastARod extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityFisherman NPC;
    FunctionHelper helper;
    int pathTimer;
    World world;
    BlockPos waterPos;
    private int fishingTime;
    private int catchingTime;
    public GoalCastARod(EntityFisherman npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper= LotsOfSteves.getHelper();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
        pathTimer=0;
        world=npc.getEntityWorld();
        waterPos=null;
        fishingTime=0;
        catchingTime=0;
    }


    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
            if(helper.isRodEquipped(NPC) && helper.isNearWater(NPC) && helper.isFreeSlotInInventory(NPC.getLocalInventory())){
                waterPos=findWaterPos();
                    return waterPos!=null;
            }
            actualDelay=0;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        actualDelay=0;
        return waterPos!=null&&helper.isRodEquipped(NPC)&&helper.isNearWater(NPC)&&helper.isFreeSlotInInventory(NPC.getLocalInventory());
    }

    @Override
    public void startExecuting() {

    }

    private int getPathTimerTimeout(){
        return 150;
    }


    @Override
    public void tick() {
        NPC.getLookController().setLookPosition(waterPos.getX(),waterPos.getY(),waterPos.getZ(),10,10);
        NPC.getLookController().tick();
        if(NPC.fishingBobber==null) {
            swingRod();
        }
        if(NPC.fishingBobber!=null) {
            fishingTime++;
            if(fishingTime>2000){
                fishingTime=0;
                if (NPC.fishingBobber != null) {
                    swingRod();
                }
            }
            if (NPC.fishingBobber.caughtEntity != null) {
                catchingTime++;
                if(catchingTime>=100+NPC.getRNG().nextInt(50)){
                    catchingTime=0;
                    swingRod();
                }
            }
        }
    }

    private BlockPos findWaterPos(){
        BlockPos result=null;
        for(int x=-3;x<=3;x++){
            for(int y=-3;y<=3;y++){
                for(int z=-3;z<=3;z++){
                    BlockPos toCheck = new BlockPos(NPC.getPosition().getX()+x,NPC.getPosition().getY()+y,NPC.getPosition().getZ()+z);
                    if(NPC.getEntityWorld().getBlockState(toCheck).getBlock() == Blocks.WATER){
                        result=toCheck;
                    }
                }
            }
        }

        return result;
    }

    private void swingRod(){
        ItemStack heldItemStack = NPC.getHeldItem(Hand.MAIN_HAND);
        int lvt_5_1_;
        if (NPC.fishingBobber != null) {
            if (!NPC.getEntityWorld().isRemote) {
                lvt_5_1_ = NPC.fishingBobber.handleHookRetraction(heldItemStack);
                heldItemStack.damageItem(lvt_5_1_, NPC, (p_220000_1_) -> {
                    p_220000_1_.sendBreakAnimation(Hand.MAIN_HAND);
                });
            }

            NPC.swingArm(Hand.MAIN_HAND);
            NPC.getEntityWorld().playSound(null, NPC.posX, NPC.posY, NPC.posZ, SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (NPC.getRNG().nextFloat() * 0.4F + 0.8F));
        } else {
            NPC.getEntityWorld().playSound(null, NPC.posX, NPC.posY, NPC.posZ, SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (NPC.getRNG().nextFloat() * 0.4F + 0.8F));
            if (!NPC.getEntityWorld().isRemote) {
                lvt_5_1_ = EnchantmentHelper.getFishingSpeedBonus(heldItemStack);
                int lvt_6_1_ = EnchantmentHelper.getFishingLuckBonus(heldItemStack);
                NPC.getEntityWorld().addEntity(new EntityCustomFishingBobber(NPC, NPC.getEntityWorld(), lvt_6_1_, lvt_5_1_));
            }

            NPC.swingArm(Hand.MAIN_HAND);
        }
    }
}