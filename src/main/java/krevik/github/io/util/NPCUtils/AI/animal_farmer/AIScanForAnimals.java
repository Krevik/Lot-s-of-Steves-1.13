package krevik.github.io.util.NPCUtils.AI.animal_farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAnimalFarmer;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import krevik.github.io.util.NPCUtils.FarmerUtils;
import krevik.github.io.util.NPCUtils.NPCUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.OverworldChunkGenerator;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class AIScanForAnimals extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAnimalFarmer NPC;
    NPCUtils npcUtils;
    World world;
    public AIScanForAnimals(EntityAnimalFarmer npc){
        NPC=npc;
        runDelay=10000/npc.getWorkSpeed();
        actualDelay=0;
        npcUtils = LotsOfSteves.getNPCUtils();
        world=npc.getEntityWorld();
    }

    public void updateRunDelay(){
        runDelay=10000/NPC.getWorkSpeed();
    }

    @Override
    public boolean shouldExecute() {
        if(NPC.getAllowed_entities()!=null){
            if(!NPC.getAllowed_entities().isEmpty()){
                if(NPC.getScanned_Living_Entities()==null){
                    runDelay=500;
                    if(NPC.getScanned_Living_Entities().isEmpty()){
                        runDelay=500;
                    }else{
                        runDelay = 10000/NPC.getWorkSpeed();
                    }
                }else{
                    runDelay = 10000/NPC.getWorkSpeed();
                }
            }
        }

        actualDelay++;
        if(actualDelay>=runDelay){
            AxisAlignedBB scanningRange = new AxisAlignedBB(NPC.getHomePosition().getX()-NPC.getWorkingRadius().getX(),NPC.getHomePosition().getY()-NPC.getWorkingRadius().getY(),NPC.getHomePosition().getZ()-NPC.getWorkingRadius().getZ(),NPC.getHomePosition().getX()+NPC.getWorkingRadius().getX(),NPC.getHomePosition().getY()+NPC.getWorkingRadius().getY(),NPC.getHomePosition().getZ()+NPC.getWorkingRadius().getZ());
            List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class,scanningRange);
            ArrayList<LivingEntity> living_entities_final_list = new ArrayList<>();
            for(int c=0;c<entities.size();c++){
                if(NPC.getAllowed_entities().contains(entities.get(c))){
                    living_entities_final_list.add(entities.get(c));
                }
            }
            NPC.setAllowed_entities(living_entities_final_list);
            actualDelay=0;
        }
        return false;
    }


    @Override
    public boolean shouldContinueExecuting() {
        actualDelay=0;
        return false;
    }

    @Override
    public void startExecuting() {
        NPC.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
    }



    @Override
    public void tick() {

    }

}
