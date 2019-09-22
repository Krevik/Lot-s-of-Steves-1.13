package krevik.github.io.entity.AI.miner;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityMiner;
import krevik.github.io.util.FunctionHelper;
import krevik.github.io.util.MiningAction;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class GoalDoStairs extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityMiner NPC;
    FunctionHelper helper;
    ArrayList<MiningAction> miningActions;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public GoalDoStairs(EntityMiner npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper=LotsOfSteves.getHelper();
        miningActions = new ArrayList<>();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
                miningActions = helper.getStairsDigPoses(NPC);
                if(!miningActions.isEmpty()){
                    return true;
                }
                actualDelay=0;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        actualDelay=0;
        return !miningActions.isEmpty();
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
            if(getIsNearDestination()||pathTimer>=getPathTimerTimeout()){

                if(miningActions.get(0).getAction()==MiningAction.ActionType.DIG && !NPC.getEntityWorld().isAirBlock(miningActions.get(0).getActionPos())) {
                    NPC.getEntityWorld().destroyBlock(miningActions.get(0).getActionPos(), true);
                }else if(miningActions.get(0).getAction()==MiningAction.ActionType.STAIRS){
                    NPC.getEntityWorld().setBlockState(miningActions.get(0).getActionPos(), Blocks.COBBLESTONE.getDefaultState());
                }


                destinationBlock=null;
                pathTimer=0;
                miningActions.remove(0);
                //safety!
                makePathSafe(NPC);
            }
        }else{
            destinationBlock = miningActions.get(0).getActionPos().up();
        }
    }

    private void makePathSafe(EntityMiner miner){
        World world = miner.getEntityWorld();
        BlockPos basePos = miner.getPosition();
        int basePosX=basePos.getX();
        int basePosY=basePos.getY();
        int basePosZ=basePos.getZ();
        for(int x=-2;x<=2;x++){
            for(int y=-2;y<2;y++){
                for(int z=-2;z<=2;z++){
                    BlockPos toCheck = new BlockPos(basePosX+x,basePosY+y,basePosZ+z);
                    Block blockToCheck = world.getBlockState(toCheck).getBlock();
                    if(blockToCheck==Blocks.LAVA || blockToCheck==Blocks.WATER){
                        if(basePosX!=basePosX+x&&basePosY!=basePosY+y&&basePosZ!=basePosZ+z) {
                            world.setBlockState(toCheck, Blocks.COBBLESTONE.getDefaultState());
                        }else{
                            if(!world.isAirBlock(basePos.up())){
                                world.destroyBlock(basePos.up(),true);
                            }
                            miner.setPositionAndUpdate(basePosX,basePosY+1,basePosZ);
                            world.setBlockState(new BlockPos(basePosX,basePosY,basePosZ),Blocks.COBBLESTONE.getDefaultState());
                        }
                        miningActions.clear();
                    }
                }
            }
        }
    }



    public double getTargetDistanceSq() {
        return 12D;
    }

    protected boolean getIsNearDestination() {
        if (this.NPC.getDistanceSq(this.destinationBlock.getX(),this.destinationBlock.getY(),this.destinationBlock.getZ()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}
