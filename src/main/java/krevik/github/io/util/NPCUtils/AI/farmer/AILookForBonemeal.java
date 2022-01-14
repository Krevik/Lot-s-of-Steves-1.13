package krevik.github.io.util.NPCUtils.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.NPCUtils.FarmerUtils;
import krevik.github.io.util.NPCUtils.NPCUtils;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class AILookForBonemeal extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoFarmer NPC;
    NPCUtils npcUtils;
    FarmerUtils farmerUtils;
    ArrayList<BlockPos> chestPoses;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public AILookForBonemeal(EntityAutoFarmer npc){
        NPC=npc;
        runDelay=1000/npc.getWorkSpeed();
        actualDelay=0;
        npcUtils = LotsOfSteves.getNPCUtils();
        farmerUtils = LotsOfSteves.getFarmerUtils();
        chestPoses=new ArrayList<>();
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
        if(actualDelay>=runDelay){
            if(!farmerUtils.isBoneMealInInventory(NPC)&&farmerUtils.isFreeSlotInInventory(NPC.getLocalInventory())){
                chestPoses=npcUtils.chestPosesWith(NPC,Items.BONE_MEAL);
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
        return !chestPoses.isEmpty()&&npcUtils.isFreeSlotInInventory(NPC.getLocalInventory());
    }

    @Override
    public void startExecuting() {
        NPC.setHeldItem(Hand.MAIN_HAND,ItemStack.EMPTY);
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
                    if(world.getTileEntity(chestPoses.get(0)) instanceof ChestTileEntity &&npcUtils.isFreeSlotInInventory(NPC.getLocalInventory())){
                        ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(chestPoses.get(0));
                        for(int c=0;c<=chest.getSizeInventory();c++){
                            if(chest.getStackInSlot(c)!=null){
                                if(!chest.getStackInSlot(c).isEmpty()){
                                    if(chest.getStackInSlot(c).getItem()!=null){
                                        if(chest.getStackInSlot(c).getItem() == Items.BONE_MEAL){
                                            ItemStack stackToTransfer = chest.getStackInSlot(c);
                                            NPC.getLocalInventory().addItem(stackToTransfer);
                                            chest.getStackInSlot(c).setCount(0);
                                            break;
                                        }
                                    }
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
        if (this.NPC.getDistanceSq(this.destinationBlock.up().getX(),this.destinationBlock.up().getY(),destinationBlock.up().getZ()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}