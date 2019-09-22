package krevik.github.io.entity.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.FunctionHelper;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class EntityAIDeliverExcessToChest extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoFarmer NPC;
    FunctionHelper helper;
    ArrayList<BlockPos> chestPoses;
    ArrayList<ItemWithInventoryIndexEntry> excessItemStacks;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public EntityAIDeliverExcessToChest(EntityAutoFarmer npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper=LotsOfSteves.getHelper();
        chestPoses=new ArrayList<>();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
            chestPoses=helper.getChestPosesWithFreeSlots(NPC);
                excessItemStacks=helper.getExcessItems(NPC);
                if(!chestPoses.isEmpty()&&!excessItemStacks.isEmpty()){
                    return true;
                }
            actualDelay=0;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        actualDelay=0;
        return !chestPoses.isEmpty()&&!excessItemStacks.isEmpty();
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
            NPC.setWhatIAmActuallyDoing("Delivering to chest");
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX()+0.5D,destinationBlock.getY()+1D,destinationBlock.getZ()+0.5D,NPC.getAIMoveSpeed());
            pathTimer++;
            if(getIsAboveDestination()||pathTimer>=getPathTimerTimeout()){
                BlockPos nearestChestPos=helper.getNearestChestPos(NPC,chestPoses);
                //BlockPos nearestChestPos=chestPoses.get(0);
                if(world.getTileEntity(nearestChestPos)!=null){
                    if(world.getTileEntity(nearestChestPos) instanceof ChestTileEntity){
                        ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(nearestChestPos);
                        tryToTransferItemsToChest(chest);
                    }
                }

                destinationBlock=null;
                pathTimer=0;
                chestPoses.clear();
            }
        }else{
            destinationBlock=helper.getNearestChestPos(NPC,chestPoses);
            //destinationBlock=chestPoses.get(0);
        }
    }

    private void tryToTransferItemsToChest(ChestTileEntity chest){
        for(int c=0;c<=15;c++){
            if(!excessItemStacks.isEmpty()) {
                if (!chest.getStackInSlot(c).isEmpty()) {
                    int actualChestItemStackSize = chest.getStackInSlot(c).getCount();
                    Item actualChestItem = chest.getStackInSlot(c).getItem();
                    if (actualChestItemStackSize < 64) {
                        for (ItemWithInventoryIndexEntry inventoryEntryItem : excessItemStacks) {
                            if (inventoryEntryItem.getItem() == actualChestItem) {
                                if (actualChestItemStackSize + NPC.getLocalInventory().getStackInSlot(inventoryEntryItem.getInventoryIndex()).getCount() > 64) {
                                    int countToDecrease = 64 - actualChestItemStackSize;
                                    chest.getStackInSlot(c).setCount(64);
                                    NPC.getLocalInventory().getStackInSlot(inventoryEntryItem.getInventoryIndex()).shrink(countToDecrease);
                                    excessItemStacks = helper.getExcessItems(NPC);
                                    break;
                                } else {
                                    chest.getStackInSlot(c).setCount(actualChestItemStackSize + NPC.getLocalInventory().getStackInSlot(inventoryEntryItem.getInventoryIndex()).getCount());
                                    NPC.getLocalInventory().getStackInSlot(inventoryEntryItem.getInventoryIndex()).setCount(0);
                                    excessItemStacks = helper.getExcessItems(NPC);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    if (!excessItemStacks.isEmpty()) {
                        chest.setInventorySlotContents(c, new ItemStack(excessItemStacks.get(0).getItem(), NPC.getLocalInventory().getStackInSlot(excessItemStacks.get(0).getInventoryIndex()).getCount()));
                        NPC.getLocalInventory().getStackInSlot(excessItemStacks.get(0).getInventoryIndex()).setCount(0);
                        excessItemStacks = helper.getExcessItems(NPC);
                        break;
                    }
                }
            }
        }
    }

    public double getTargetDistanceSq() {
        return 6D;
    }

    protected boolean getIsAboveDestination() {
        if (this.NPC.getDistanceSq(this.destinationBlock.up().getX(),this.destinationBlock.up().getY(),this.destinationBlock.up().getZ()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}
