package krevik.github.io.entity;

import com.google.common.collect.ImmutableSet;
import krevik.github.io.entity.AI.lumberjack.*;
import krevik.github.io.init.ModEntities;
import krevik.github.io.init.ModItems;
import krevik.github.io.util.WorkingRadius;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class EntityAutoLumberjack extends AnimalEntity {

    private final Inventory localInventory = new Inventory(64);
    private static final DataParameter<Integer> MODE = EntityDataManager.createKey(EntityAutoLumberjack.class, DataSerializers.VARINT);

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
                Items.DARK_OAK_SAPLING, Items.JUNGLE_SAPLING,
                Items.OAK_SAPLING, Items.SPRUCE_SAPLING));
        ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.ACACIA_SAPLING, Items.BIRCH_SAPLING,
                Items.DARK_OAK_SAPLING, Items.JUNGLE_SAPLING,
                Items.OAK_SAPLING, Items.SPRUCE_SAPLING, Items.BONE_MEAL,
                Item.getItemFromBlock(Blocks.ACACIA_LOG),Item.getItemFromBlock(Blocks.BIRCH_LOG),
                Item.getItemFromBlock(Blocks.DARK_OAK_LOG),Item.getItemFromBlock(Blocks.JUNGLE_LOG),
                Item.getItemFromBlock(Blocks.OAK_LOG),Item.getItemFromBlock(Blocks.SPRUCE_LOG), Items.APPLE));
        LOGS_ALLOWED.addAll(ImmutableSet.of(Blocks.ACACIA_LOG, Blocks.BIRCH_LOG, Blocks.DARK_OAK_LOG,
                Blocks.JUNGLE_LOG,Blocks.OAK_LOG,Blocks.SPRUCE_LOG));
    }

    public EntityAutoLumberjack(EntityType<EntityAutoLumberjack> entityAutoLumberjackEntityType, World world) {
        super(entityAutoLumberjackEntityType, world);
        setAIMoveSpeed(1f);
        setCanPickUpLoot(false);
        workingRadius=new WorkingRadius(15,15,15);
        SAPLINGS.addAll(ImmutableSet.of(Items.ACACIA_SAPLING, Items.BIRCH_SAPLING,
                Items.DARK_OAK_SAPLING, Items.JUNGLE_SAPLING,
                Items.OAK_SAPLING, Items.SPRUCE_SAPLING));
        ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.ACACIA_SAPLING, Items.BIRCH_SAPLING,
                Items.DARK_OAK_SAPLING, Items.JUNGLE_SAPLING,
                Items.OAK_SAPLING, Items.SPRUCE_SAPLING, Items.BONE_MEAL,
                Item.getItemFromBlock(Blocks.ACACIA_LOG),Item.getItemFromBlock(Blocks.BIRCH_LOG),
                Item.getItemFromBlock(Blocks.DARK_OAK_LOG),Item.getItemFromBlock(Blocks.JUNGLE_LOG),
                Item.getItemFromBlock(Blocks.OAK_LOG),Item.getItemFromBlock(Blocks.SPRUCE_LOG), Items.APPLE));
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
        goalSelector.addGoal(4,new EntityAILookForTools(this));
        goalSelector.addGoal(4,new EntityAIEquipTool(this));
        goalSelector.addGoal(5,new AILumberjackCollectLoot(this));
        goalSelector.addGoal(5,new AILumberjackHarvestLogs(this));
        goalSelector.addGoal(6,new AILumberjackDeliverExcessToChest(this));
        goalSelector.addGoal(8,new AILumberjackFertilizeSaplings(this));
        goalSelector.addGoal(5,new AILumberjackLookForBonemeal(this));
        goalSelector.addGoal(4,new AILumberjackLookForSaplings(this));
        goalSelector.addGoal(6,new AILumberjackPlantSaplings(this));
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        for(int c=0;c<=getLocalInventory().getSizeInventory();c++){
            if(!getLocalInventory().getStackInSlot(c).isEmpty()){
                ItemEntity itemEntity = new ItemEntity(world,posX,posY,posZ);
                itemEntity.setItem(getLocalInventory().getStackInSlot(c));
                itemEntity.setPosition(posX+0.5,posY+2,posZ+0.5);
                itemEntity.setMotion(new Vec3d(-0.5+getRNG().nextFloat(),-0.1f,-0.5+getRNG().nextFloat()));
                world.addEntity(itemEntity);
            }
        }
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
        tag.putDouble("homeX",getHomePosition().getX());
        tag.putDouble("homeY",getHomePosition().getY());
        tag.putDouble("homeZ",getHomePosition().getZ());
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
        setHomePosAndDistance(new BlockPos(tag.getDouble("homeX"),tag.getDouble("homeY"),tag.getDouble("homeZ")),15);
    }

    public ItemStack getItemStackToRender(int mode){
        ItemStack result = new ItemStack(Items.OAK_SAPLING);
        if(mode==0) result = new ItemStack(ModItems.MULTI_SAPLING);
        if(mode==1) result = new ItemStack(Items.OAK_SAPLING);
        if(mode==2) result = new ItemStack(Items.BIRCH_SAPLING);
        if(mode==3) result = new ItemStack(Items.DARK_OAK_SAPLING);
        if(mode==4) result = new ItemStack(Items.ACACIA_SAPLING);
        if(mode==5) result = new ItemStack(Items.SPRUCE_SAPLING);
        if(mode==6) result = new ItemStack(Items.JUNGLE_SAPLING);

        return result;
    }

    private void handleOperatableItems(){
        int mode = getMode();
        LOGS_ALLOWED.clear();
        ITEMS_OF_INTEREST.clear();
        SAPLINGS.clear();
        if(mode==0){
            SAPLINGS.addAll(ImmutableSet.of(Items.ACACIA_SAPLING, Items.BIRCH_SAPLING,
                    Items.DARK_OAK_SAPLING, Items.JUNGLE_SAPLING,
                    Items.OAK_SAPLING, Items.SPRUCE_SAPLING));
            ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.ACACIA_SAPLING, Items.BIRCH_SAPLING,
                    Items.DARK_OAK_SAPLING, Items.JUNGLE_SAPLING,
                    Items.OAK_SAPLING, Items.SPRUCE_SAPLING, Items.BONE_MEAL,
                    Item.getItemFromBlock(Blocks.ACACIA_LOG),Item.getItemFromBlock(Blocks.BIRCH_LOG),
                    Item.getItemFromBlock(Blocks.DARK_OAK_LOG),Item.getItemFromBlock(Blocks.JUNGLE_LOG),
                    Item.getItemFromBlock(Blocks.OAK_LOG),Item.getItemFromBlock(Blocks.SPRUCE_LOG), Items.APPLE));
            LOGS_ALLOWED.addAll(ImmutableSet.of(Blocks.ACACIA_LOG, Blocks.BIRCH_LOG, Blocks.DARK_OAK_LOG,
                    Blocks.JUNGLE_LOG,Blocks.OAK_LOG,Blocks.SPRUCE_LOG));
        }
        if(mode==1){
            SAPLINGS.addAll(ImmutableSet.of(Items.OAK_SAPLING));
            ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.OAK_SAPLING, Items.BONE_MEAL,
                    Item.getItemFromBlock(Blocks.OAK_LOG), Items.APPLE));
            LOGS_ALLOWED.addAll(ImmutableSet.of(Blocks.OAK_LOG));
        }
        if(mode==2){
            SAPLINGS.addAll(ImmutableSet.of(Items.BIRCH_SAPLING));
            ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.BIRCH_SAPLING, Items.BONE_MEAL,
                    Item.getItemFromBlock(Blocks.BIRCH_LOG), Items.APPLE));
            LOGS_ALLOWED.addAll(ImmutableSet.of(Blocks.BIRCH_LOG));
        }
        if(mode==3){
            SAPLINGS.addAll(ImmutableSet.of(Items.DARK_OAK_SAPLING));
            ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.DARK_OAK_SAPLING, Items.BONE_MEAL,
                    Item.getItemFromBlock(Blocks.DARK_OAK_LOG), Items.APPLE));
            LOGS_ALLOWED.addAll(ImmutableSet.of(Blocks.DARK_OAK_LOG));
        }
        if(mode==4){
            SAPLINGS.addAll(ImmutableSet.of(Items.ACACIA_SAPLING));
            ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.ACACIA_SAPLING, Items.BONE_MEAL,
                    Item.getItemFromBlock(Blocks.ACACIA_LOG), Items.APPLE));
            LOGS_ALLOWED.addAll(ImmutableSet.of(Blocks.ACACIA_LOG));
        }
        if(mode==5){
            SAPLINGS.addAll(ImmutableSet.of(Items.SPRUCE_SAPLING));
            ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.SPRUCE_SAPLING, Items.BONE_MEAL,
                    Item.getItemFromBlock(Blocks.SPRUCE_LOG), Items.APPLE));
            LOGS_ALLOWED.addAll(ImmutableSet.of(Blocks.SPRUCE_LOG));
        }
        if(mode==6){
            SAPLINGS.addAll(ImmutableSet.of(Items.JUNGLE_SAPLING));
            ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.JUNGLE_SAPLING, Items.BONE_MEAL,
                    Item.getItemFromBlock(Blocks.JUNGLE_LOG), Items.APPLE));
            LOGS_ALLOWED.addAll(ImmutableSet.of(Blocks.JUNGLE_LOG));
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(MODE, 0);
    }

    public int getMode()
    {
        return MathHelper.clamp(getDataManager().get(MODE), 0, 16);
    }

    public void setMode(int meta) {
        getDataManager().set(MODE, meta);
        handleOperatableItems();
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
        if(!world.isRemote) {
            if (getMode() < 6) {
                setMode(getMode() + 1);
            } else {
                setMode(0);
            }
        }
        return ActionResultType.SUCCESS;
    }
}
