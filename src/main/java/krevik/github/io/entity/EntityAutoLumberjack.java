package krevik.github.io.entity;

import com.google.common.collect.ImmutableSet;
import krevik.github.io.entity.AI.lumberjack.*;
import krevik.github.io.init.ModEntities;
import krevik.github.io.util.WorkingRadius;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class EntityAutoLumberjack extends AnimalEntity {

    private final Inventory localInventory = new Inventory(64);

    public ArrayList<Block> LOGS_ALLOWED = new ArrayList<>();
    public ArrayList<Item> ITEMS_OF_INTEREST = new ArrayList<>();
    public ArrayList<Item> SAPLINGS = new ArrayList<>();
    private WorkingRadius workingRadius;



    public EntityAutoLumberjack(World p_i48568_2_) {
        super((EntityType<? extends AnimalEntity>) ModEntities.ENTITY_AUTO_LUMBERJACK, p_i48568_2_);
        setAIMoveSpeed(1f);
        setCanPickUpLoot(false);
        workingRadius=new WorkingRadius(15,15,15);
        SAPLINGS.addAll(ImmutableSet.of(Items.ACACIA_SAPLING, Items.BIRCH_SAPLING,
                Items.DARK_OAK_SAPLING, Items.JUNGLE_SAPLING,Items.OAK_SAPLING,Items.SPRUCE_SAPLING));
        ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.ACACIA_SAPLING, Items.BIRCH_SAPLING,
                Items.DARK_OAK_SAPLING, Items.JUNGLE_SAPLING,Items.OAK_SAPLING,Items.SPRUCE_SAPLING, Items.BONE_MEAL,
                Item.getItemFromBlock(Blocks.ACACIA_LOG),Item.getItemFromBlock(Blocks.BIRCH_LOG),
                Item.getItemFromBlock(Blocks.DARK_OAK_LOG),Item.getItemFromBlock(Blocks.JUNGLE_LOG),
                Item.getItemFromBlock(Blocks.OAK_LOG),Item.getItemFromBlock(Blocks.SPRUCE_LOG)));
        LOGS_ALLOWED.addAll(ImmutableSet.of(Blocks.ACACIA_LOG, Blocks.BIRCH_LOG, Blocks.DARK_OAK_LOG,
                Blocks.JUNGLE_LOG,Blocks.OAK_LOG,Blocks.SPRUCE_LOG));
    }

    public EntityAutoLumberjack(EntityType<EntityAutoLumberjack> entityAutoLumberjackEntityType, World world) {
        super(entityAutoLumberjackEntityType, world);
        setAIMoveSpeed(1f);
        setCanPickUpLoot(false);
        workingRadius=new WorkingRadius(15,15,15);
        SAPLINGS.addAll(ImmutableSet.of(Items.ACACIA_SAPLING, Items.BIRCH_SAPLING,
                Items.DARK_OAK_SAPLING, Items.JUNGLE_SAPLING,Items.OAK_SAPLING,Items.SPRUCE_SAPLING));
        ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.ACACIA_SAPLING, Items.BIRCH_SAPLING,
                Items.DARK_OAK_SAPLING, Items.JUNGLE_SAPLING,Items.OAK_SAPLING,Items.SPRUCE_SAPLING, Items.BONE_MEAL,
                Item.getItemFromBlock(Blocks.ACACIA_LOG),Item.getItemFromBlock(Blocks.BIRCH_LOG),
                Item.getItemFromBlock(Blocks.DARK_OAK_LOG),Item.getItemFromBlock(Blocks.JUNGLE_LOG),
                Item.getItemFromBlock(Blocks.OAK_LOG),Item.getItemFromBlock(Blocks.SPRUCE_LOG)));
        LOGS_ALLOWED.addAll(ImmutableSet.of(Blocks.ACACIA_LOG, Blocks.BIRCH_LOG, Blocks.DARK_OAK_LOG,
                Blocks.JUNGLE_LOG,Blocks.OAK_LOG,Blocks.SPRUCE_LOG));
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    public WorkingRadius getWorkingRadius(){
        return workingRadius;
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(5,new EntityAILookForTools(this));
        goalSelector.addGoal(5,new EntityAIEquipTool(this));
        goalSelector.addGoal(5,new AILumberjackCollectLoot(this));
        goalSelector.addGoal(5,new AILumberjackHarvestLogs(this));
        goalSelector.addGoal(6,new AILumberjackDeliverExcessToChest(this));
        goalSelector.addGoal(8,new AILumberjackFertilizeSaplings(this));
        goalSelector.addGoal(5,new AILumberjackLookForBonemeal(this));
        goalSelector.addGoal(5,new AILumberjackLookForSaplings(this));
        goalSelector.addGoal(7,new AILumberjackPlantSaplings(this));
    }

    @Override
    public void tick() {
        super.tick();
        setAIMoveSpeed(1f);
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
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
