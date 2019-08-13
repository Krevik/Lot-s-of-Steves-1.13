package krevik.github.io.entity.AI.lumberjack;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.entity.EntityAutoLumberjack;
import krevik.github.io.util.FunctionHelper;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import net.minecraft.block.BeetrootBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class AILumberjackFertilizeSaplings extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoLumberjack NPC;
    FunctionHelper helper;
    ArrayList<BlockPos> FertilizableSaplings;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public AILumberjackFertilizeSaplings(EntityAutoLumberjack npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper= LotsOfSteves.getHelper();
        FertilizableSaplings = new ArrayList<>();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
            if(helper.isBoneMealInInventoryLumberjack(NPC)){
                FertilizableSaplings = helper.getFertilizableSaplings(NPC);
                if(!FertilizableSaplings.isEmpty()){
                    return true;
                }
            }
            actualDelay=0;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        actualDelay=0;
        return !FertilizableSaplings.isEmpty()&&helper.isBoneMealInInventoryLumberjack(NPC);
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
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX()+0.5D,destinationBlock.getY(),destinationBlock.getZ()+0.5D,NPC.getAIMoveSpeed());
            pathTimer++;
            if(getIsNearDestination()||pathTimer>=getPathTimerTimeout()){
                if(world.getBlockState(FertilizableSaplings.get(0)).getBlock() instanceof SaplingBlock) {
                        if (helper.isBoneMealInInventoryLumberjack(NPC)) {
                            ArrayList<ItemWithInventoryIndexEntry> bonemealEntries = helper.getBoneMealWithIndexesInInventoryLumberjack(NPC.getLocalInventory());
                            if (!bonemealEntries.isEmpty()) {
                                ItemWithInventoryIndexEntry itemEntry = bonemealEntries.get(NPC.getRNG().nextInt(bonemealEntries.size()));
                                        if (!world.isRemote) {
                                            world.playEvent(2005, FertilizableSaplings.get(0), 0);
                                        }
                                        if(world.getBlockState(FertilizableSaplings.get(0)).getBlock() instanceof SaplingBlock) {
                                            ((SaplingBlock) world.getBlockState(FertilizableSaplings.get(0)).getBlock()).grow(world, FertilizableSaplings.get(0), world.getBlockState(FertilizableSaplings.get(0)), NPC.getRNG());
                                            NPC.getLocalInventory().getStackInSlot(itemEntry.getInventoryIndex()).shrink(1);
                                        }
                            }
                        }
                }

                destinationBlock=null;
                pathTimer=0;
                FertilizableSaplings.remove(0);
            }
        }else{
            destinationBlock = FertilizableSaplings.get(0);
        }
    }

    public double getTargetDistanceSq() {
        return 6D;
    }

    protected boolean getIsNearDestination() {
        if (this.NPC.getDistanceSq(this.destinationBlock.getX(),this.destinationBlock.getY(),this.destinationBlock.getZ()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}
