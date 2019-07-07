package krevik.github.io.entity.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.FunctionHelper;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EntityAIPlantSeeds extends EntityAIBase {

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
        setMutexBits(5);
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
        return !wetFarmlands.isEmpty();
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
            if(getIsAboveDestination()||pathTimer>=getPathTimerTimeout()){
                if(world.getBlockState(wetFarmlands.get(0)).getBlock() == Blocks.FARMLAND){
                    if(world.getBlockState(wetFarmlands.get(0)).get(BlockFarmland.MOISTURE)>2){
                        ArrayList<ItemWithInventoryIndexEntry> itemEntries = helper.getSeedsWithInventoryIndexes(NPC);
                        if(!itemEntries.isEmpty()) {
                            ItemWithInventoryIndexEntry itemEntry = itemEntries.get(NPC.getRNG().nextInt(itemEntries.size()));
                            NPC.setHeldItem(EnumHand.MAIN_HAND,new ItemStack(itemEntry.getItem()));
                            IBlockState stateToPlant = helper.getBlockStateToPlant(itemEntry.getItem());
                            if (world.isAirBlock(wetFarmlands.get(0).up())) {
                                if (world.isAirBlock(wetFarmlands.get(0).up(2))) {
                                    world.setBlockState(wetFarmlands.get(0).up(), stateToPlant, 3);
                                    NPC.getLocalInventory().getStackInSlot(itemEntry.getInventoryIndex()).shrink(1);
                                }
                            }
                            NPC.setHeldItem(EnumHand.MAIN_HAND,ItemStack.EMPTY);
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
        return 1.25D;
    }

    protected boolean getIsAboveDestination() {
        if (this.NPC.getDistanceSqToCenter(this.destinationBlock.up()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}
