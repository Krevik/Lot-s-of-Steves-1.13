package krevik.github.io.entity.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.FunctionHelper;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import net.minecraft.block.BeetrootBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class EntityAIFertilizePlants extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoFarmer NPC;
    FunctionHelper helper;
    ArrayList<BlockPos> FertilizablePlants;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public EntityAIFertilizePlants(EntityAutoFarmer npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper=LotsOfSteves.getHelper();
        FertilizablePlants = new ArrayList<>();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
            if(helper.isBoneMealInInventory(NPC)){
                FertilizablePlants = helper.getFertilizablePlants(NPC);
                if(!FertilizablePlants.isEmpty()){
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
        return !FertilizablePlants.isEmpty()&&helper.isBoneMealInInventory(NPC);
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
                NPC.setHeldItem(Hand.MAIN_HAND,new ItemStack(Items.BONE_MEAL,1));
                if(world.getBlockState(FertilizablePlants.get(0).up()).has(CropsBlock.AGE)) {
                    if (world.getBlockState(FertilizablePlants.get(0).up()).get(CropsBlock.AGE)<7) {
                            if (helper.isBoneMealInInventory(NPC)) {
                                ArrayList<ItemWithInventoryIndexEntry> bonemealEntries = helper.getBoneMealWithIndexesInInventory(NPC.getLocalInventory(), NPC);
                                if (!bonemealEntries.isEmpty()) {
                                    ItemWithInventoryIndexEntry itemEntry = bonemealEntries.get(NPC.getRNG().nextInt(bonemealEntries.size()));
                                    if (world.isAirBlock(FertilizablePlants.get(0).up(2))) {
                                        if (world.isAirBlock(FertilizablePlants.get(0).up(3))) {
                                            if (!world.isRemote) {
                                                world.playEvent(2005, FertilizablePlants.get(0).up(), 0);
                                            }
                                            ((CropsBlock) world.getBlockState(FertilizablePlants.get(0).up()).getBlock()).grow(world, FertilizablePlants.get(0).up(), world.getBlockState(FertilizablePlants.get(0).up()));
                                            NPC.getLocalInventory().getStackInSlot(itemEntry.getInventoryIndex()).shrink(1);
                                        }
                                    }
                                }
                            }
                    }
                }

                if(world.getBlockState(FertilizablePlants.get(0).up()).has(BeetrootBlock.BEETROOT_AGE)) {
                    if (world.getBlockState(FertilizablePlants.get(0).up()).get(BeetrootBlock.BEETROOT_AGE)<3) {
                        if (helper.isBoneMealInInventory(NPC)) {
                            ArrayList<ItemWithInventoryIndexEntry> bonemealEntries = helper.getBoneMealWithIndexesInInventory(NPC.getLocalInventory(), NPC);
                            if (!bonemealEntries.isEmpty()) {
                                ItemWithInventoryIndexEntry itemEntry = bonemealEntries.get(NPC.getRNG().nextInt(bonemealEntries.size()));
                                if (world.isAirBlock(FertilizablePlants.get(0).up(2))) {
                                    if (world.isAirBlock(FertilizablePlants.get(0).up(3))) {
                                        if (!world.isRemote) {
                                            world.playEvent(2005, FertilizablePlants.get(0).up(), 0);
                                        }
                                        ((BeetrootBlock) world.getBlockState(FertilizablePlants.get(0).up()).getBlock()).grow(world, FertilizablePlants.get(0).up(), world.getBlockState(FertilizablePlants.get(0).up()));
                                        NPC.getLocalInventory().getStackInSlot(itemEntry.getInventoryIndex()).shrink(1);
                                    }
                                }
                            }
                        }
                    }
                }

                destinationBlock=null;
                pathTimer=0;
                FertilizablePlants.remove(0);
                NPC.setHeldItem(Hand.MAIN_HAND,ItemStack.EMPTY);
            }
        }else{
            destinationBlock = FertilizablePlants.get(0);
        }
    }

    public double getTargetDistanceSq() {
        return 6D;
    }

    protected boolean getIsNearDestination() {
        if (this.NPC.getDistanceSq(this.destinationBlock.getX(),this.destinationBlock.up().getY(),this.destinationBlock.getZ()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}
