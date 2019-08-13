package krevik.github.io.entity.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.FunctionHelper;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class AIFarmerCollectLoot extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoFarmer NPC;
    FunctionHelper helper;
    ArrayList<ItemEntity> pickableLoot;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public AIFarmerCollectLoot(EntityAutoFarmer npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper=LotsOfSteves.getHelper();
        pickableLoot = new ArrayList<>();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        pickableLoot=helper.getPickableLoot(NPC);
        actualDelay++;
        if(actualDelay>=runDelay||!pickableLoot.isEmpty()){
                pickableLoot = helper.getPickableLoot(NPC);
                if(!pickableLoot.isEmpty()&&helper.isFreeSlotInInventory(NPC.getLocalInventory())){
                    return true;
                }
            actualDelay=0;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        actualDelay=0;
        return !pickableLoot.isEmpty()&&helper.isFreeSlotInInventory(NPC.getLocalInventory());
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
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX()+0.5,destinationBlock.getY()+1,destinationBlock.getZ()+0.5,NPC.getAIMoveSpeed());
            pathTimer++;
            if(getIsNearDestination()||pathTimer>=getPathTimerTimeout()){
                if(!pickableLoot.isEmpty()&&helper.isFreeSlotInInventory(NPC.getLocalInventory())){
                    ItemStack stackToPickup = pickableLoot.get(0).getItem();
                    if(!stackToPickup.isEmpty()){
                        if(helper.isFreeSlotInInventory(NPC.getLocalInventory())){
                            NPC.getLocalInventory().addItem(stackToPickup);
                        }
                    }
                }
                destinationBlock=null;
                pathTimer=0;
                if(pickableLoot.get(0)!=null) {
                    pickableLoot.get(0).remove();
                    pickableLoot.remove(0);
                }
            }
        }else{
            destinationBlock = pickableLoot.get(0).getPosition();
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
