package krevik.github.io.entity.AI.lumberjack;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.entity.EntityAutoLumberjack;
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

public class AILumberjackLookForSaplings extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoLumberjack NPC;
    FunctionHelper helper;
    ArrayList<BlockPos> chestPoses;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public AILumberjackLookForSaplings(EntityAutoLumberjack npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper= LotsOfSteves.getHelper();
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
            if(!helper.areAllSaplingsOfInterestInInventory(NPC)&&helper.isFreeSlotInInventory(NPC.getLocalInventory())){
                chestPoses=helper.chestPosesWithSaplingsThatAreNotInInventoryButAreInInterest(NPC);
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
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX()+0.5D,destinationBlock.getY()+1D,destinationBlock.getZ()+0.5D,NPC.getAIMoveSpeed());
            pathTimer++;
            if(getIsAboveDestination()||pathTimer>=getPathTimerTimeout()){
                if(world.getTileEntity(chestPoses.get(0))!=null){
                    if(world.getTileEntity(chestPoses.get(0)) instanceof ChestTileEntity && helper.isFreeSlotInInventory(NPC.getLocalInventory())){
                        ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(chestPoses.get(0));
                        boolean hasAcacia=false;
                        boolean hasOak=false;
                        boolean hasJungle=false;
                        boolean hasDarkOak=false;
                        boolean hasSpruce=false;
                        boolean hasBirch=false;
                        for(int c=0;c<NPC.getLocalInventory().getSizeInventory();c++){
                            if(!NPC.getLocalInventory().getStackInSlot(c).isEmpty()){
                                Item item = NPC.getLocalInventory().getStackInSlot(c).getItem();
                                if(item == Items.ACACIA_SAPLING){
                                    hasAcacia=true;
                                }
                                if(item == Items.OAK_SAPLING){
                                    hasOak = true;
                                }
                                if(item == Items.DARK_OAK_SAPLING){
                                    hasDarkOak = true;
                                }
                                if(item == Items.JUNGLE_SAPLING){
                                    hasJungle=true;
                                }
                                if(item == Items.SPRUCE_SAPLING){
                                    hasSpruce=true;
                                }
                                if(item == Items.BIRCH_SAPLING){
                                    hasBirch=true;
                                }
                            }
                        }
                        for(int c=0;c<=15;c++){
                            if(!chest.getStackInSlot(c).isEmpty()){
                                Item item = chest.getStackInSlot(c).getItem();
                                if(item == Items.ACACIA_SAPLING && !hasAcacia){
                                    ItemStack stackToTransfer = chest.getStackInSlot(c);
                                    NPC.getLocalInventory().addItem(stackToTransfer);
                                    chest.getStackInSlot(c).setCount(0);
                                    hasAcacia=true;
                                }
                                if(item == Items.OAK_SAPLING && !hasOak){
                                    ItemStack stackToTransfer = chest.getStackInSlot(c);
                                    NPC.getLocalInventory().addItem(stackToTransfer);
                                    chest.getStackInSlot(c).setCount(0);
                                    hasOak=true;
                                }
                                if(item == Items.DARK_OAK_SAPLING && !hasDarkOak){
                                    ItemStack stackToTransfer = chest.getStackInSlot(c);
                                    NPC.getLocalInventory().addItem(stackToTransfer);
                                    chest.getStackInSlot(c).setCount(0);
                                    hasDarkOak=true;
                                }
                                if(item == Items.JUNGLE_SAPLING && !hasJungle){
                                    ItemStack stackToTransfer = chest.getStackInSlot(c);
                                    NPC.getLocalInventory().addItem(stackToTransfer);
                                    chest.getStackInSlot(c).setCount(0);
                                    hasJungle=true;
                                }
                                if(item == Items.SPRUCE_SAPLING && !hasSpruce){
                                    ItemStack stackToTransfer = chest.getStackInSlot(c);
                                    NPC.getLocalInventory().addItem(stackToTransfer);
                                    chest.getStackInSlot(c).setCount(0);
                                    hasSpruce=true;
                                }
                                if(item == Items.BIRCH_SAPLING && !hasBirch){
                                    ItemStack stackToTransfer = chest.getStackInSlot(c);
                                    NPC.getLocalInventory().addItem(stackToTransfer);
                                    chest.getStackInSlot(c).setCount(0);
                                    hasBirch=true;
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
