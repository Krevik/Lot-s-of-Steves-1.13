package krevik.github.io.entity.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.FunctionHelper;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class EntityAIPlantSeeds extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoFarmer NPC;
    FunctionHelper helper;
    ArrayList<BlockPos> wetFarmlands;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public EntityAIPlantSeeds(EntityAutoFarmer npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper=LotsOfSteves.getHelper();
        wetFarmlands =new ArrayList<>();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
            if(helper.areAnySeedsInInventory(NPC)){
                wetFarmlands = helper.getAvailableFarmlands(NPC);
                if(!wetFarmlands.isEmpty()){
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
        return !wetFarmlands.isEmpty() && helper.areAnySeedsInInventory(NPC);
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
                if(world.getBlockState(wetFarmlands.get(0)).getBlock() == Blocks.FARMLAND){
                    if(world.getBlockState(wetFarmlands.get(0)).get(FarmlandBlock.MOISTURE)>2){
                        ArrayList<ItemWithInventoryIndexEntry> itemEntries = helper.getSeedsWithInventoryIndexes(NPC);
                        if(!itemEntries.isEmpty()) {
                            ItemWithInventoryIndexEntry itemEntry = itemEntries.get(NPC.getRNG().nextInt(itemEntries.size()));
                            NPC.setHeldItem(Hand.MAIN_HAND,new ItemStack(itemEntry.getItem()));
                            BlockState stateToPlant = helper.getBlockStateToPlant(itemEntry.getItem());
                            if (world.isAirBlock(wetFarmlands.get(0).up())) {
                                if (world.isAirBlock(wetFarmlands.get(0).up(2))) {
                                    world.setBlockState(wetFarmlands.get(0).up(), stateToPlant, 3);
                                    NPC.getLocalInventory().getStackInSlot(itemEntry.getInventoryIndex()).shrink(1);
                                }
                            }
                            NPC.setHeldItem(Hand.MAIN_HAND,ItemStack.EMPTY);
                        }
                    }
                }

                destinationBlock=null;
                pathTimer=0;
                wetFarmlands.remove(0);
            }
        }else{
            destinationBlock= wetFarmlands.get(0);
        }
    }

    public double getTargetDistanceSq() {
        return 6D;
    }

    protected boolean getIsNearDestination() {
        if (this.NPC.getDistanceSq(this.destinationBlock.getX(),destinationBlock.getY(),destinationBlock.getZ()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}
