package krevik.github.io.entity;

import krevik.github.io.entity.AI.miner.GoalDoStairs;
import krevik.github.io.init.ModEntities;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class EntityMiner extends AnimalEntity {

    private final Inventory localInventory = new Inventory(64);
    private int[][][] stairsPattern = new int[4][256][4];
    public EntityMiner(World world) {
        super((EntityType<? extends AnimalEntity>) ModEntities.ENTITY_MINER, world);
        setAIMoveSpeed(1f);
        setCanPickUpLoot(false);
    }

    public EntityMiner(EntityType<EntityMiner> entityAutoFarmerEntityType, World world) {
        super(entityAutoFarmerEntityType, world);
        setAIMoveSpeed(1f);
        setCanPickUpLoot(false);
    }

    private void clearAndInitializeStairsPattern(){
        stairsPattern = new int[4][256][4];
        /*for(int i1=0;i1<=10;i1++){
            for(int i2=0;i2<=10;i2++){
                for(int i3=0;i3<=10;i3++){
                    stairsPattern[i1][i2][i3]=0;
                }
            }
        }*/
        for(int i1=0;i1<=3;i1++){
            for(int i2=0;i2<256;i2++){
                for(int i3=0;i3<=3;i3++){
                    stairsPattern[i1][i2][i3]=0;
                }
            }
        }
        makeHolePattern();
    }

    public int[][][] getStairsPattern() {
        clearAndInitializeStairsPattern();
        return stairsPattern;
    }

    private ArrayList<Integer> getActualYRangeStairs(int height){
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(int x=0;x<5;x++){
            result.add(height+x);
        }
        return result;
    }

    private void makeStairsPatternX(){
        for(int i1=0;i1<=10;i1++){
            for(int i2=0;i2<=10;i2++){
                ArrayList<Integer> actualYRange = getActualYRangeStairs(i1);
                for(int i3=0;i3<=10;i3++){
                   if(actualYRange.contains(i2)){
                       stairsPattern[i1][i2][i3]=1;
                   }
                }
            }
        }
    }

    private void makeHolePattern(){
        int switcher=1;
            for(int y=255;y>8;y--){
                switcher++;
                if(switcher>=13){
                    switcher=1;
                }
                for(int z=0;z<=3;z++){
                    for(int x=0;x<=3;x++){
                    if(shouldPlaceStairBlockAt(switcher,x,z)) {
                        stairsPattern[x][y][z] = 1;
                    }
                }
            }
        }
    }

    private boolean shouldPlaceStairBlockAt(int switcher, int x,int z){
        boolean result=false;
        if(switcher==1&&x==0&&z==0) result=true;
        if(switcher==2&&x==1&&z==0) result=true;
        if(switcher==3&&x==2&&z==0) result=true;
        if(switcher==4&&x==3&&z==0) result=true;
        if(switcher==5&&x==3&&z==1) result=true;
        if(switcher==6&&x==3&&z==2) result=true;
        if(switcher==7&&x==3&&z==3) result=true;
        if(switcher==8&&x==2&&z==3) result=true;
        if(switcher==9&&x==1&&z==3) result=true;
        if(switcher==10&&x==0&&z==3) result=true;
        if(switcher==11&&x==0&&z==2) result=true;
        if(switcher==12&&x==0&&z==1) result=true;

        return result;
    }


    @Override
    protected void registerData() {
        super.registerData();
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

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        setHomePosAndDistance(getPosition(),30);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        setAIMoveSpeed(1f);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1,new GoalDoStairs(this));
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

        tag.putDouble("homeX",getHomePosition().getX());
        tag.putDouble("homeY",getHomePosition().getY());
        tag.putDouble("homeZ",getHomePosition().getZ());
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
        setHomePosAndDistance(new BlockPos(tag.getDouble("homeX"),tag.getDouble("homeY"),tag.getDouble("homeZ")),30);
    }
}
