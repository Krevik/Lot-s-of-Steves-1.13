package krevik.github.io.entity;

import krevik.github.io.entity.AI.lumberjack.EntityAIEquipTool;
import krevik.github.io.entity.AI.lumberjack.EntityAILookForTools;
import krevik.github.io.init.ModEntities;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityAutoLumberjack extends AnimalEntity {

    private final Inventory localInventory = new Inventory(64);

    private static int workRadius = 15;
    public EntityAutoLumberjack(World p_i48568_2_) {
        super((EntityType<? extends AnimalEntity>) ModEntities.ENTITY_AUTO_LUMBERJACK, p_i48568_2_);
        setAIMoveSpeed(1f);
        setCanPickUpLoot(false);
    }

    public EntityAutoLumberjack(EntityType<EntityAutoLumberjack> entityAutoLumberjackEntityType, World world) {
        super(entityAutoLumberjackEntityType, world);
        setAIMoveSpeed(1f);
        setCanPickUpLoot(false);
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    public int getWorkRadius(){
        return workRadius;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(5,new EntityAILookForTools(this));
        goalSelector.addGoal(5,new EntityAIEquipTool(this));
    }

    @Override
    public void tick() {
        super.tick();
        setAIMoveSpeed(1f);
    }


    @Override
    protected void registerData() {
        super.registerData();
    }


    public Inventory getLocalInventory() {
        return this.localInventory;
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3000000417232513D);
    }

    @Nullable
    @Override
    public AgeableEntity createChild(AgeableEntity entityAgeable) {
        return null;
    }


    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        ListNBT nbttaglist = new ListNBT();
        for(int i = 0; i < this.localInventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.localInventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                nbttaglist.add(itemstack.write(new CompoundNBT()));
            }
        }
        tag.put("Inventory", nbttaglist);
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);

        ListNBT nbttaglist = tag.getList("Inventory", 10);

        for(int i = 0; i < nbttaglist.size(); ++i) {
            ItemStack itemstack = ItemStack.read(nbttaglist.getCompound(i));
            if (!itemstack.isEmpty()) {
                this.localInventory.addItem(itemstack);
            }
        }
    }
}
