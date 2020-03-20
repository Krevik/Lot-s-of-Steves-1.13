package krevik.github.io.util.NPCUtils.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.NPCUtils.FarmerUtils;
import krevik.github.io.util.NPCUtils.NPCUtils;
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
    NPCUtils npcUtils;
    FarmerUtils farmerUtils;
    ArrayList<ItemEntity> pickableLoot;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public AIFarmerCollectLoot(EntityAutoFarmer npc){
        NPC=npc;
        runDelay=250/npc.getWorkSpeed();
        actualDelay=0;
        npcUtils = LotsOfSteves.getNPCUtils();
        farmerUtils = LotsOfSteves.getFarmerUtils();
        pickableLoot = new ArrayList<>();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    public void updateRunDelay(){
        runDelay=250/NPC.getWorkSpeed();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
            pickableLoot = farmerUtils.getPickableLoot(NPC);
            if(!pickableLoot.isEmpty()&&npcUtils.isFreeSlotInInventory(NPC.getLocalInventory())){
                return true;
            }
            actualDelay=0;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        actualDelay=0;
        return !pickableLoot.isEmpty()&&npcUtils.isFreeSlotInInventory(NPC.getLocalInventory());
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
                if(!pickableLoot.isEmpty()&&npcUtils.isFreeSlotInInventory(NPC.getLocalInventory())){
                    ItemStack stackToPickup = pickableLoot.get(0).getItem();
                    if(!stackToPickup.isEmpty()){
                        if(npcUtils.isFreeSlotInInventory(NPC.getLocalInventory())){
                            NPC.getLocalInventory().addItem(stackToPickup);
                            if(pickableLoot.get(0)!=null) {
                                pickableLoot.get(0).remove();
                                pickableLoot.remove(0);
                            }
                        }
                    }
                }
                destinationBlock=null;
                pathTimer=0;

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
