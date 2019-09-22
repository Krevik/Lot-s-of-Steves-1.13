package krevik.github.io.util;

import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ToolType;

public class MiningAction {
    private BlockPos actionPos;
    private ActionType actionName;
    public MiningAction(BlockPos pos, ActionType action){
        actionPos=pos;
        actionName=action;
    }

    public BlockPos getActionPos(){
        return actionPos;
    }

    public ActionType getAction(){
        return actionName;
    }

    public void setActionPos(BlockPos pos){
        actionPos=pos;
    }

    public void setActionName(ActionType name){
        actionName=name;
    }



    public enum ActionType{
        DIG,STAIRS;
    }
}
