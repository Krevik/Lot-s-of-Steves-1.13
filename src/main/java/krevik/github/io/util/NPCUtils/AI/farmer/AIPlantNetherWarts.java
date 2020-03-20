package krevik.github.io.util.NPCUtils.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import krevik.github.io.util.NPCUtils.FarmerUtils;
import krevik.github.io.util.NPCUtils.NPCUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class AIPlantNetherWarts extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoFarmer NPC;
    FarmerUtils farmerUtils;
    NPCUtils npcUtils;
    ArrayList<BlockPos> plantableSoulSands;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public AIPlantNetherWarts(EntityAutoFarmer npc){
        NPC=npc;
        runDelay=1000/npc.getWorkSpeed();
        actualDelay=0;
        farmerUtils = LotsOfSteves.getFarmerUtils();
        npcUtils = LotsOfSteves.getNPCUtils();
        plantableSoulSands = new ArrayList<>();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    public void updateRunDelay(){
        runDelay=1000/NPC.getWorkSpeed();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(NPC.getGENERAL_ALLOWED_ITEMS().contains(Items.NETHER_WART)) {
            if (actualDelay >= runDelay) {
                if (npcUtils.isInInventory(NPC.getLocalInventory(), Items.NETHER_WART)) {
                    plantableSoulSands = farmerUtils.getAvailableSoulBlocksForNetherWarts(NPC);
                    if (!plantableSoulSands.isEmpty()) {
                        return true;
                    }
                }
                actualDelay = 0;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        actualDelay=0;
        return !plantableSoulSands.isEmpty() && farmerUtils.isInInventory(NPC.getLocalInventory(),Items.NETHER_WART);
    }

    @Override
    public void startExecuting() {
        NPC.setHeldItem(Hand.MAIN_HAND,new ItemStack(Items.NETHER_WART));
    }

    private int getPathTimerTimeout(){
        return 150;
    }

    @Override
    public void tick() {
        if(destinationBlock!=null){
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX()+0.5D,destinationBlock.getY()+1D,destinationBlock.getZ()+0.5D,NPC.getAIMoveSpeed());
            pathTimer++;
            NPC.setHeldItem(Hand.MAIN_HAND,new ItemStack(Items.NETHER_WART));
            if(getIsNearDestination()||pathTimer>=getPathTimerTimeout()){
                if(world.getBlockState(plantableSoulSands.get(0)).getBlock() == Blocks.SOUL_SAND){
                        ArrayList<ItemWithInventoryIndexEntry> itemEntries = farmerUtils.getItemsWithIndexesInInventory(NPC.getLocalInventory(),Items.NETHER_WART);
                        if(!itemEntries.isEmpty()) {
                            ItemWithInventoryIndexEntry itemEntry = itemEntries.get(NPC.getRNG().nextInt(itemEntries.size()));
                            BlockState stateToPlant = farmerUtils.getBlockStateToPlant(itemEntry.getItem());
                            if (world.isAirBlock(plantableSoulSands.get(0).up())) {
                                if (world.isAirBlock(plantableSoulSands.get(0).up(2))) {
                                    world.setBlockState(plantableSoulSands.get(0).up(), stateToPlant, 3);
                                    NPC.getLocalInventory().getStackInSlot(itemEntry.getInventoryIndex()).shrink(1);
                                }
                            }
                            NPC.setHeldItem(Hand.MAIN_HAND,ItemStack.EMPTY);
                        }
                }

                destinationBlock=null;
                pathTimer=0;
                plantableSoulSands.remove(0);
                NPC.setHeldItem(Hand.MAIN_HAND,ItemStack.EMPTY);
            }
        }else{
            destinationBlock= plantableSoulSands.get(0);
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
