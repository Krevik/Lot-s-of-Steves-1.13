package krevik.github.io.entity;

import krevik.github.io.entity.AI.farmer.*;
import krevik.github.io.init.ModEntities;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityAutoFarmer extends EntityAnimal {


    private final InventoryBasic localInventory = new InventoryBasic(new TextComponentString("Items"), 64);

    private static int workRadius = 15;
    public EntityAutoFarmer(World p_i48576_2_) {
        super(ModEntities.ENTITY_AUTO_FARMER, p_i48576_2_);
        this.setSize(0.6F, 1.95F);
        setAIMoveSpeed(1f);
        setCanPickUpLoot(false);
    }


    @Override
    public boolean canDespawn() {
        return false;
    }

    public int getWorkRadius(){
        return workRadius;
    }

    @Override
    public void tick() {
        super.tick();
        setAIMoveSpeed(1f);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(5,new EntityAILookForSeeds(this));
        tasks.addTask(5,new EntityAILookForBonemeal(this));
        tasks.addTask(5,new EntityAIPlantSeeds(this));
        tasks.addTask(6,new EntityAIFertilizePlants(this));
        tasks.addTask(5,new EntityAIHarvestFarmland(this));
        tasks.addTask(5,new EntityAICollectLoot(this));
        tasks.addTask(6,new EntityAIDeliverExcessToChest(this));
    }

    @Override
    public boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
        return super.processInteract(p_184645_1_, p_184645_2_);
    }

    @Override
    protected void registerData() {
        super.registerData();
    }


    public InventoryBasic getLocalInventory() {
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
    public EntityAgeable createChild(EntityAgeable entityAgeable) {
        return null;
    }

    @Nullable
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
        return super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
    }

    @Override
    public void writeAdditional(NBTTagCompound tag) {
        super.writeAdditional(tag);
        NBTTagList nbttaglist = new NBTTagList();
        for(int i = 0; i < this.localInventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.localInventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                nbttaglist.add((INBTBase)itemstack.write(new NBTTagCompound()));
            }
        }
        tag.setTag("Inventory", nbttaglist);
    }

    @Override
    public void readAdditional(NBTTagCompound tag) {
        super.readAdditional(tag);

        NBTTagList nbttaglist = tag.getList("Inventory", 10);

        for(int i = 0; i < nbttaglist.size(); ++i) {
            ItemStack itemstack = ItemStack.read(nbttaglist.getCompound(i));
            if (!itemstack.isEmpty()) {
                this.localInventory.addItem(itemstack);
            }
        }
    }
}
