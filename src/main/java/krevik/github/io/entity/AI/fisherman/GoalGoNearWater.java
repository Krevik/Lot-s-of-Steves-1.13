package krevik.github.io.entity.AI.fisherman;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityFisherman;
import krevik.github.io.util.FunctionHelper;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import net.minecraft.block.BeetrootBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class GoalGoNearWater extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityFisherman NPC;
    FunctionHelper helper;
    ArrayList<BlockPos> safePositionsNearWater;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public GoalGoNearWater(EntityFisherman npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper= LotsOfSteves.getHelper();
        safePositionsNearWater = new ArrayList<>();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
            if(!helper.isNearWater(NPC)&&helper.isRodEquipped(NPC)) {
                safePositionsNearWater = helper.getSafePositionsNearWater(NPC);
                if (!safePositionsNearWater.isEmpty()) {
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
        return !safePositionsNearWater.isEmpty()&&!helper.isNearWater(NPC)&&helper.isRodEquipped(NPC);
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
            NPC.setWhatIAmActuallyDoing("Going near water");
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX()+0.5D,destinationBlock.getY()+1D,destinationBlock.getZ()+0.5D,NPC.getAIMoveSpeed());
            pathTimer++;
            if(getIsNearDestination()||pathTimer>=getPathTimerTimeout()){
                destinationBlock=null;
                pathTimer=0;
                safePositionsNearWater.remove(0);
            }
        }else{
            destinationBlock = helper.getNearestSafePosNearWater(NPC,safePositionsNearWater);
        }
    }



    public double getTargetDistanceSq() {
        return 2D;
    }

    protected boolean getIsNearDestination() {
        if (this.NPC.getDistanceSq(this.destinationBlock.getX(),this.destinationBlock.getY(),this.destinationBlock.getZ()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}
