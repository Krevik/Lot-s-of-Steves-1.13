package krevik.github.io.entity.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.FunctionHelper;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityAICollectLoot extends EntityAIBase {

    private int runDelay;
    private int actualDelay;
    private EntityAutoFarmer NPC;
    FunctionHelper helper;
    ArrayList<EntityItem> pickableLoot;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public EntityAICollectLoot(EntityAutoFarmer npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper=LotsOfSteves.getHelper();
        pickableLoot = new ArrayList<>();
        setMutexBits(5);
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
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX(),destinationBlock.getY()-1,destinationBlock.getZ(),NPC.getAIMoveSpeed());
            pathTimer++;
            if(getIsNearDestination()||pathTimer>=getPathTimerTimeout()){
                if(!pickableLoot.isEmpty()&&helper.isFreeSlotInInventory(NPC.getLocalInventory())){
                    ItemStack stackToPickup = pickableLoot.get(0).getItem();
                    if(!stackToPickup.isEmpty()){
                        if(helper.isFreeSlotInInventory(NPC.getLocalInventory())){
                            NPC.getLocalInventory().addItem(stackToPickup);
                            pickableLoot.get(0).remove();
                        }
                    }else{
                        destinationBlock=null;
                        pathTimer=0;
                        pickableLoot.remove(0);
                    }
                }
                destinationBlock=null;
                pathTimer=0;
                pickableLoot.remove(0);
            }
        }else{
            destinationBlock = pickableLoot.get(0).getPosition();
        }
    }

    public double getTargetDistanceSq() {
        return 2.5D;
    }

    protected boolean getIsNearDestination() {
        if (this.NPC.getDistanceSqToCenter(this.destinationBlock) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}
