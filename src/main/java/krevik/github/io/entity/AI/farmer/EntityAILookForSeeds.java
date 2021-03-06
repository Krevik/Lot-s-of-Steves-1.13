package krevik.github.io.entity.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.FunctionHelper;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class EntityAILookForSeeds extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoFarmer NPC;
    FunctionHelper helper;
    ArrayList<BlockPos> chestPoses;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public EntityAILookForSeeds(EntityAutoFarmer npc){
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
            if(!helper.areAllSeedsOfInterestInInventory(NPC)&&helper.isFreeSlotInInventory(NPC.getLocalInventory())){
                chestPoses=helper.chestPosesWithSeedsThatAreNotInInventoryButAreInInterest(NPC);
                if(!chestPoses.isEmpty()){
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
        return !chestPoses.isEmpty()&&helper.isFreeSlotInInventory(NPC.getLocalInventory());
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
            NPC.setWhatIAmActuallyDoing("Going for seeds");
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX()+0.5D,destinationBlock.getY()+1D,destinationBlock.getZ()+0.5D,NPC.getAIMoveSpeed());
            pathTimer++;
                if(getIsAboveDestination()||pathTimer>=getPathTimerTimeout()){
                    if(world.getTileEntity(chestPoses.get(0))!=null){
                        if(world.getTileEntity(chestPoses.get(0)) instanceof ChestTileEntity && helper.isFreeSlotInInventory(NPC.getLocalInventory())){
                            ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(chestPoses.get(0));
                            boolean hasCarrots=false;
                            boolean hasPotatoes=false;
                            boolean hasBeetrotSeeds=false;
                            boolean hasWheatSeeds=false;
                            for(int c=0;c<NPC.getLocalInventory().getSizeInventory();c++){
                                if(!NPC.getLocalInventory().getStackInSlot(c).isEmpty()){
                                    Item item = NPC.getLocalInventory().getStackInSlot(c).getItem();
                                    if(item == Items.BEETROOT_SEEDS){
                                        hasBeetrotSeeds=true;
                                    }
                                    if(item == Items.POTATO){
                                        hasPotatoes = true;
                                    }
                                    if(item == Items.CARROT){
                                        hasCarrots = true;
                                    }
                                    if(item == Items.WHEAT_SEEDS){
                                        hasWheatSeeds=true;
                                    }
                                }
                            }
                            for(int c=0;c<=15;c++){
                                if(!chest.getStackInSlot(c).isEmpty()){
                                    Item item = chest.getStackInSlot(c).getItem();
                                    if(item == Items.CARROT && !hasCarrots){
                                        ItemStack stackToTransfer = chest.getStackInSlot(c);
                                        NPC.getLocalInventory().addItem(stackToTransfer);
                                        chest.getStackInSlot(c).setCount(0);
                                        hasCarrots=true;
                                    }
                                    if(item == Items.POTATO && !hasPotatoes){
                                        ItemStack stackToTransfer = chest.getStackInSlot(c);
                                        NPC.getLocalInventory().addItem(stackToTransfer);
                                        chest.getStackInSlot(c).setCount(0);
                                        hasPotatoes=true;
                                    }
                                    if(item == Items.BEETROOT_SEEDS && !hasBeetrotSeeds){
                                        ItemStack stackToTransfer = chest.getStackInSlot(c);
                                        NPC.getLocalInventory().addItem(stackToTransfer);
                                        chest.getStackInSlot(c).setCount(0);
                                        hasBeetrotSeeds=true;
                                    }
                                    if(item == Items.WHEAT_SEEDS && !hasWheatSeeds){
                                        ItemStack stackToTransfer = chest.getStackInSlot(c);
                                        NPC.getLocalInventory().addItem(stackToTransfer);
                                        chest.getStackInSlot(c).setCount(0);
                                        hasWheatSeeds=true;
                                    }
                                }
                            }
                        }
                    }

                    destinationBlock=null;
                    pathTimer=0;
                    chestPoses.remove(0);
                }
        }else{
            destinationBlock=chestPoses.get(0);
        }
    }

    public double getTargetDistanceSq() {
        return 1.75D;
    }

    protected boolean getIsAboveDestination() {
        if (this.NPC.getDistanceSq(this.destinationBlock.up().getX(),destinationBlock.up().getY(),destinationBlock.up().getZ()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}
