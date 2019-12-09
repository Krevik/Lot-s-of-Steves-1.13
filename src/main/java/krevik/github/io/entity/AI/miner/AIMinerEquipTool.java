package krevik.github.io.entity.AI.miner;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoLumberjack;
import krevik.github.io.entity.EntityAutoMiner;
import krevik.github.io.util.FunctionHelper;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class AIMinerEquipTool extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoMiner NPC;
    FunctionHelper helper;
    World world;
    public AIMinerEquipTool(EntityAutoMiner npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper= LotsOfSteves.getHelper();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
        world=npc.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
            if(helper.areToolsInInventory(NPC)&&!helper.isToolEquipped(NPC)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        actualDelay=0;
        return !helper.isToolEquipped(NPC) && helper.areToolsInInventory(NPC);
    }

    @Override
    public void startExecuting() {

    }

    @Override
    public void tick() {
        NPC.setWhatIAmActuallyDoing("Equipping tool");
        if(!helper.isToolEquipped(NPC)){
            if(helper.areToolsInInventory(NPC)){
                ArrayList<ItemWithInventoryIndexEntry> availableTools = helper.getAvailableToolsInInventory(NPC);
                ItemStack stackToEquip = NPC.getLocalInventory().getStackInSlot(availableTools.get(0).getInventoryIndex());
                NPC.setHeldItem(Hand.MAIN_HAND,stackToEquip);
                //NPC.getLocalInventory().getStackInSlot(availableTools.get(0).getInventoryIndex()).setCount(0);
            }
        }
    }
}
