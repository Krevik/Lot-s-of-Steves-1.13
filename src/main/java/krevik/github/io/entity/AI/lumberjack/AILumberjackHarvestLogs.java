package krevik.github.io.entity.AI.lumberjack;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoLumberjack;
import krevik.github.io.util.FunctionHelper;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;

public class AILumberjackHarvestLogs extends Goal{
    private int runDelay;
    private int actualDelay;
    private int actualDiggingTime;
    private float desiredDiggingTime;
    private EntityAutoLumberjack NPC;
    FunctionHelper helper;
    ArrayList<BlockPos> LOGS_POSITIONS;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public AILumberjackHarvestLogs(EntityAutoLumberjack npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        actualDiggingTime=0;
        desiredDiggingTime=0;
        helper= LotsOfSteves.getHelper();
        LOGS_POSITIONS = new ArrayList<>();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.TARGET));
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
            LOGS_POSITIONS = helper.getLogsToHarvest(NPC);
            if(!LOGS_POSITIONS.isEmpty()){
                return true;
            }
            actualDelay=0;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        actualDelay=0;
        return !LOGS_POSITIONS.isEmpty() && !(NPC.getRNG().nextInt(500)==0);
    }

    @Override
    public void startExecuting() {

    }

    private int getPathTimerTimeout(){
        return 150;
    }

    @Override
    public void tick() {
        if(destinationBlock!=null){
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX()+0.5D,destinationBlock.getY()+1D,destinationBlock.getZ()+0.5D,NPC.getAIMoveSpeed());
            pathTimer++;
            if(getIsNearDestination()||pathTimer>=getPathTimerTimeout()){
                if(pathTimer>getPathTimerTimeout()&& !getIsNearDestination()){
                    destinationBlock=null;
                    pathTimer=0;
                    LOGS_POSITIONS.remove(helper.getNearestTree(NPC,LOGS_POSITIONS));
                }else{
                    //check if the block was not previously harvested and if it is in LOGS ALLOWED list
                    if(NPC.LOGS_ALLOWED.contains(world.getBlockState(helper.getNearestTree(NPC,LOGS_POSITIONS)).getBlock())){
                        //prepare harvesting info and start harvesting
                        BlockPos positionToHarvest = helper.getNearestTree(NPC,LOGS_POSITIONS);
                        BlockState harvestBlockState = world.getBlockState(positionToHarvest);
                        Block blockToHarvest = harvestBlockState.getBlock();
                        Block saplingToReplant = helper.getSaplingFromLogBlock(blockToHarvest);
                        int i1 =  30;
                        float f = harvestBlockState.getBlockHardness(NPC.getEntityWorld(),positionToHarvest) * (float)(i1 + 1);
                        float i2 = getDigSpeed(NPC,harvestBlockState,positionToHarvest) / f / (float)i1;
                        desiredDiggingTime=5;
                        NPC.world.sendBlockBreakProgress(NPC.getEntityId(),positionToHarvest,actualDiggingTime);
                            NPC.setActiveHand(Hand.MAIN_HAND);
                            NPC.swingArm(Hand.MAIN_HAND);
                        actualDiggingTime++;
                        //destroy block
                        if(actualDiggingTime>=desiredDiggingTime){
                            world.destroyBlock(positionToHarvest,true);
                            destinationBlock=null;
                            pathTimer=0;
                            //damage item experimental
                            PlayerEntity player = NPC.getEntityWorld().getClosestPlayer(NPC.getPosition().getX(),NPC.getPosition().getY(),NPC.getPosition().getZ());
                            if(player!=null){
                                NPC.getHeldItem(Hand.MAIN_HAND).damageItem(1, player, (p_220045_0_) -> {
                                    p_220045_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
                                });
                            }
                            LOGS_POSITIONS.remove(helper.getNearestTree(NPC,LOGS_POSITIONS));
                            actualDiggingTime=0;
                            desiredDiggingTime=0;
                            //try to replant
                            if(helper.isInInventoryLumberjack(NPC,Item.getItemFromBlock(saplingToReplant))) {
                                if (world.getBlockState(positionToHarvest.down()).getBlock() == Blocks.GRASS_BLOCK ||
                                        world.getBlockState(positionToHarvest.down()).getBlock() == Blocks.PODZOL ||
                                        world.getBlockState(positionToHarvest.down()).getBlock() == Blocks.DIRT ||
                                        world.getBlockState(positionToHarvest.down()).getBlock() == Blocks.COARSE_DIRT)
                                {
                                    ItemWithInventoryIndexEntry saplingEntry = helper.getItemWithInventoryIndexLumberjack(NPC,Item.getItemFromBlock(saplingToReplant));
                                    world.setBlockState(positionToHarvest.down(),saplingToReplant.getDefaultState());
                                    NPC.getLocalInventory().getStackInSlot(saplingEntry.getInventoryIndex()).shrink(1);
                                }
                            }
                        }
                    }else{
                        destinationBlock=null;
                        pathTimer=0;
                        LOGS_POSITIONS.remove(helper.getNearestTree(NPC,LOGS_POSITIONS));
                        actualDiggingTime=0;
                        desiredDiggingTime=0;
                    }
                }
            }
        }else{
            destinationBlock = helper.getNearestTree(NPC,LOGS_POSITIONS);
        }
    }



    public double getTargetDistanceSq() {
        return 6D;
    }

    protected boolean getIsNearDestination() {
        if (this.NPC.getDistanceSq(this.destinationBlock.getX(),NPC.getPosition().getY(),this.destinationBlock.getZ()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

    public float getDigSpeed(EntityAutoLumberjack npc, BlockState state, @Nullable BlockPos pos) {
        float f;
        if(npc.getHeldItem(Hand.MAIN_HAND).isEmpty()){
            f=1f;
        }else{
            f=npc.getHeldItem(Hand.MAIN_HAND).getItem().getDestroySpeed(npc.getHeldItem(Hand.MAIN_HAND),state);
        }
        if (f > 1.0F) {
            int i = EnchantmentHelper.getEfficiencyModifier(npc);
            ItemStack itemstack = npc.getHeldItem(Hand.MAIN_HAND);
            if (i > 0 && !itemstack.isEmpty()) {
                f += (float)(i * i + 1);
            }
        }

        if (EffectUtils.hasMiningSpeedup(npc)) {
            f *= 1.0F + (float)(EffectUtils.getMiningSpeedup(npc) + 1) * 0.2F;
        }

        if (npc.isPotionActive(Effects.MINING_FATIGUE)) {
            float f1;
            switch(npc.getActivePotionEffect(Effects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (npc.areEyesInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(npc)) {
            f /= 5.0F;
        }

        if (!npc.onGround) {
            f /= 5.0F;
        }
        return f;
    }
}
