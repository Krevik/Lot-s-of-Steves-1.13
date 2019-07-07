package krevik.github.io.entity.AI.lumberjack;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoLumberjack;
import krevik.github.io.util.FunctionHelper;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.ArrayList;


public class EntityAIEquipTool extends EntityAIBase {

    private int runDelay;
    private int actualDelay;
    private EntityAutoLumberjack NPC;
    FunctionHelper helper;
    World world;
    public EntityAIEquipTool(EntityAutoLumberjack npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper=LotsOfSteves.getHelper();
        setMutexBits(5);
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
        if(!helper.isToolEquipped(NPC)){
            if(helper.areToolsInInventory(NPC)){
                ArrayList<ItemWithInventoryIndexEntry> availableTools = helper.getAvailableToolsInInventory(NPC);
                ItemStack stackToEquip = NPC.getLocalInventory().getStackInSlot(availableTools.get(0).getInventoryIndex());
                NPC.setHeldItem(EnumHand.MAIN_HAND,stackToEquip);
                NPC.getLocalInventory().getStackInSlot(availableTools.get(0).getInventoryIndex()).setCount(0);
            }
        }
    }
}
