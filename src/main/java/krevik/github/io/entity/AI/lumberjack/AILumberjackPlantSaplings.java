package krevik.github.io.entity.AI.lumberjack;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoLumberjack;
import krevik.github.io.util.FunctionHelper;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import net.minecraft.block.Block;
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

public class AILumberjackPlantSaplings extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoLumberjack NPC;
    FunctionHelper helper;
    ArrayList<BlockPos> emptyPlantPlaces;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public AILumberjackPlantSaplings(EntityAutoLumberjack npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper=LotsOfSteves.getHelper();
        emptyPlantPlaces=new ArrayList<>();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
            if(helper.areAnySaplingsInInventory(NPC)){
                emptyPlantPlaces = helper.getEmptyPlantPlaces(NPC);
                if(!emptyPlantPlaces.isEmpty()){
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
        return !emptyPlantPlaces.isEmpty() && helper.areAnySaplingsInInventory(NPC);
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
                Block blockToCheck = world.getBlockState(emptyPlantPlaces.get(0)).getBlock();
                if(blockToCheck==Blocks.GRASS_BLOCK||blockToCheck==Blocks.DIRT||blockToCheck==Blocks.PODZOL||blockToCheck==Blocks.COARSE_DIRT){
                    if(world.isAirBlock(emptyPlantPlaces.get(0).up())){
                        ArrayList<ItemWithInventoryIndexEntry> itemEntries = helper.getSaplingsWithInventoryIndexes(NPC);
                        if(!itemEntries.isEmpty()) {
                            ItemWithInventoryIndexEntry itemEntry = itemEntries.get(NPC.getRNG().nextInt(itemEntries.size()));
                            BlockState stateToPlant = Block.getBlockFromItem(itemEntry.getItem()).getDefaultState();
                            if (world.isAirBlock(emptyPlantPlaces.get(0).up(2))) {
                                if (world.isAirBlock(emptyPlantPlaces.get(0).up(3))) {
                                    world.setBlockState(emptyPlantPlaces.get(0).up(), stateToPlant, 3);
                                    NPC.getLocalInventory().getStackInSlot(itemEntry.getInventoryIndex()).shrink(1);
                                }
                            }
                        }
                    }
                }

                destinationBlock=null;
                pathTimer=0;
                emptyPlantPlaces.remove(0);
            }
        }else{
            destinationBlock= emptyPlantPlaces.get(0);
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
