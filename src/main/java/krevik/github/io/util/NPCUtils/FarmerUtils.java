package krevik.github.io.util.NPCUtils;

import com.google.common.collect.ImmutableList;
import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.entity.EntityNPC;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import net.minecraft.block.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import org.lwjgl.system.CallbackI;
import org.omg.DynamicAny.DynEnumHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FarmerUtils extends NPCUtils {
    public boolean areAllSeedsOfInterestInInventory(EntityAutoFarmer farmer){
        ArrayList<Item> seedsOfInterest;
        ArrayList<Item> generalItemsOfInterest = farmer.getGENERAL_ALLOWED_ITEMS();
        seedsOfInterest = createSeedsOfInterestListFromGeneralItemsOfInterestList(generalItemsOfInterest);
        int seedsNumberFound = 0;
        if(seedsOfInterest!=null){
            if(!seedsOfInterest.isEmpty()){
                for(Item item: seedsOfInterest){
                    innerLoop: for(int c=0;c<farmer.getLocalInventory().getSizeInventory();c++){
                        ItemStack stackInSlot = farmer.getLocalInventory().getStackInSlot(c);
                        if(stackInSlot!=null){
                            if(!stackInSlot.isEmpty()){
                                if(stackInSlot.getItem()!=null){
                                    if(stackInSlot.getItem() == item){
                                        seedsNumberFound++;
                                        break innerLoop;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return seedsNumberFound==seedsOfInterest.size();
    }

    public ArrayList<Item> createSeedsOfInterestListFromGeneralItemsOfInterestList(ArrayList<Item> generalItemsOfInterest){
        ArrayList<Item> result = new ArrayList<>();
        if(generalItemsOfInterest.contains(Items.CARROT)) result.add(Items.CARROT);
        if(generalItemsOfInterest.contains(Items.POTATO)) result.add(Items.POTATO);
        if(generalItemsOfInterest.contains(Items.WHEAT) || generalItemsOfInterest.contains(Items.WHEAT_SEEDS)) result.add(Items.WHEAT_SEEDS);
        if(generalItemsOfInterest.contains(Items.BEETROOT) || generalItemsOfInterest.contains(Items.BEETROOT_SEEDS)) result.add(Items.BEETROOT_SEEDS);
        if(generalItemsOfInterest.contains(Items.NETHER_WART)) result.add(Items.NETHER_WART);
        if(generalItemsOfInterest.contains(Items.COCOA_BEANS)) result.add(Items.COCOA_BEANS);
        if(generalItemsOfInterest.contains(Items.SUGAR_CANE)) result.add(Items.SUGAR_CANE);
        return result;
    }

    public ArrayList<Item> createSeedsOfInterestListThatAreNotInInventory(EntityNPC NPC, ArrayList<Item> generalItemsOfInterest){
        ArrayList<Item> result = new ArrayList<>();
        ArrayList<Item> allInterestingSeeds = createSeedsOfInterestListFromGeneralItemsOfInterestList(generalItemsOfInterest);
        for(Item item: allInterestingSeeds){
            if(!LotsOfSteves.getNPCUtils().isInInventory(NPC.getLocalInventory(),item)){
                result.add(item);
            }
        }

        return result;
    }

    public ArrayList<BlockPos> chestPosesWithSeedsThatAreNotInInventoryButAreInInterest(EntityAutoFarmer farmer){
        ArrayList<BlockPos> result=new ArrayList<>();
        ArrayList<Item> seedsOfInterestThatAreNotInInventory = new ArrayList<>();
        ArrayList<Item> seedsOfInterest;
        ArrayList<Item> generalItemsOfInterest =farmer.getGENERAL_ALLOWED_ITEMS();
        seedsOfInterest = createSeedsOfInterestListFromGeneralItemsOfInterestList(generalItemsOfInterest);
        for(Item item: seedsOfInterest){
            if(!LotsOfSteves.getNPCUtils().isInInventory(farmer.getLocalInventory(),item)){
                seedsOfInterestThatAreNotInInventory.add(item);
            }
        }

        World world = farmer.getEntityWorld();
        int xRadius = farmer.getWorkingRadius().getX();
        int yRadius = farmer.getWorkingRadius().getY();
        int zRadius = farmer.getWorkingRadius().getZ();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck = new BlockPos(farmer.getHomePosition().getX()+x,farmer.getHomePosition().getY()+y,farmer.getHomePosition().getZ()+z);
                    if(world.getTileEntity(toCheck)!=null){
                        if(world.getTileEntity(toCheck) instanceof ChestTileEntity){
                            ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(toCheck);
                            outerloop: for(Item itemOfInterest:seedsOfInterestThatAreNotInInventory){
                                boolean foundTheItem = false;
                                for(int c=0;c<chest.getSizeInventory();c++){
                                    if(chest.getStackInSlot(c)!=null){
                                        if(!chest.getStackInSlot(c).isEmpty()){
                                            if(chest.getStackInSlot(c).getItem()!=null){
                                                if(chest.getStackInSlot(c).getItem() == itemOfInterest && !foundTheItem){
                                                    result.add(toCheck);
                                                    foundTheItem=true;
                                                    continue outerloop;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public boolean isBoneMealInInventory(EntityAutoFarmer farmer){
        boolean isBoneMealHere=false;
        for(int c=0;c<farmer.getLocalInventory().getSizeInventory();c++){
            if(!farmer.getLocalInventory().getStackInSlot(c).isEmpty()) {
                Item item = farmer.getLocalInventory().getStackInSlot(c).getItem();
                if (item==Items.BONE_MEAL) {
                    isBoneMealHere = true;
                    break;
                }
            }
        }
        return isBoneMealHere;
    }

    public ArrayList<ItemEntity> getPickableLoot(EntityAutoFarmer npc){
        ArrayList<ItemEntity> result=new ArrayList<>();
        ArrayList<Item> itemsOfInterest = getItemsOfInterest(npc);
        int xRadius = npc.getWorkingRadius().getX();
        int yRadius = npc.getWorkingRadius().getY();
        int zRadius = npc.getWorkingRadius().getZ();
        World world = npc.getEntityWorld();

        List<ItemEntity> e = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(npc.getHomePosition().getX() - xRadius, npc.getHomePosition().getY() - yRadius, npc.getHomePosition().getZ() - zRadius, npc.getHomePosition().getX() + xRadius, npc.getHomePosition().getY() + yRadius, npc.getHomePosition().getZ() + zRadius));
        if(!e.isEmpty()){
            for(ItemEntity item:e){
                if(itemsOfInterest.contains(item.getItem().getItem())) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    public ArrayList<Item> getItemsOfInterest(EntityAutoFarmer npc){
        ArrayList<Item> result = new ArrayList<>();
        ArrayList<Item> generalItemsOfInterest = npc.getGENERAL_ALLOWED_ITEMS();
        if(generalItemsOfInterest.contains(Items.CARROT)){
            result.add(Items.CARROT);
            result.add(Items.CARROT_ON_A_STICK);
            result.add(Items.GOLDEN_CARROT);
        }
        if(generalItemsOfInterest.contains(Items.POTATO)){
            result.add(Items.POTATO);
            result.add(Items.BAKED_POTATO);
            result.add(Items.POISONOUS_POTATO);
        }
        if(generalItemsOfInterest.contains(Items.WHEAT) || generalItemsOfInterest.contains(Items.WHEAT_SEEDS)){
            result.add(Items.WHEAT_SEEDS);
            result.add(Items.WHEAT);
        }
        if(generalItemsOfInterest.contains(Items.BEETROOT) || generalItemsOfInterest.contains(Items.BEETROOT_SEEDS)){
            result.add(Items.BEETROOT);
            result.add(Items.BEETROOT_SEEDS);
            result.add(Items.BEETROOT_SOUP);
        }
        if(generalItemsOfInterest.contains(Items.SUGAR_CANE)){
            result.add(Items.SUGAR_CANE);
            result.add(Items.SUGAR);
            result.add(Items.PAPER);
        }
        if(generalItemsOfInterest.contains(Items.COCOA_BEANS)){
            result.add(Items.COCOA_BEANS);
        }
        if(generalItemsOfInterest.contains(Items.NETHER_WART)){
            result.add(Items.NETHER_WART);
            result.add(Items.NETHER_WART_BLOCK);
        }
        return result;
    }

    public ArrayList<Block> getInterestingPlantBlocks(EntityAutoFarmer npc){
        ArrayList<Block> result = new ArrayList<>();
        ArrayList<Item> generalItemsOfInterest = npc.getGENERAL_ALLOWED_ITEMS();
        if(generalItemsOfInterest!=null) {
            if(!generalItemsOfInterest.isEmpty()) {
                if (generalItemsOfInterest.contains(Items.CARROT)) {
                    result.add(Blocks.CARROTS);
                }
                if (generalItemsOfInterest.contains(Items.POTATO)) {
                    result.add(Blocks.POTATOES);
                }
                if (generalItemsOfInterest.contains(Items.WHEAT) || generalItemsOfInterest.contains(Items.WHEAT_SEEDS)) {
                    result.add(Blocks.WHEAT);
                }
                if (generalItemsOfInterest.contains(Items.BEETROOT) || generalItemsOfInterest.contains(Items.BEETROOT_SEEDS)) {
                    result.add(Blocks.BEETROOTS);
                }
                if (generalItemsOfInterest.contains(Items.SUGAR_CANE)) {
                    result.add(Blocks.SUGAR_CANE);
                }
                if (generalItemsOfInterest.contains(Items.COCOA_BEANS)) {
                    result.add(Blocks.COCOA);
                }
                if (generalItemsOfInterest.contains(Items.NETHER_WART)) {
                    result.add(Blocks.NETHER_WART);
                }
            }
        }

        return result;
    }

    public ArrayList<BlockPos> getFertilizablePlantsThatAreInInterest(EntityAutoFarmer npc){
        ArrayList<Block> interestingPlantBlocks = getInterestingPlantBlocks(npc);
        if(interestingPlantBlocks.contains(Blocks.SUGAR_CANE)){
            interestingPlantBlocks.remove(Blocks.SUGAR_CANE);
        }
        if(interestingPlantBlocks.contains(Blocks.NETHER_WART)){
            interestingPlantBlocks.remove(Blocks.NETHER_WART);
        }

        ArrayList<BlockPos> result=new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getX();
        int yRadius = npc.getWorkingRadius().getY();
        int zRadius = npc.getWorkingRadius().getZ();
        World world = npc.getEntityWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck=new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getBlockState(toCheck)!=null){
                        if(world.getBlockState(toCheck).getBlock() instanceof CropsBlock){
                            CropsBlock crop = (CropsBlock) world.getBlockState(toCheck).getBlock();
                            if (interestingPlantBlocks.contains(crop)) {
                                if(!crop.isMaxAge(world.getBlockState(toCheck))){
                                    if(world.isAirBlock(toCheck.up())){
                                        if(world.isAirBlock(toCheck.up(2))){
                                            result.add(toCheck.down());
                                        }
                                    }
                                }
                            }
                        }else if(world.getBlockState(toCheck).getBlock() instanceof CocoaBlock){
                            CocoaBlock cocoaBlock = (CocoaBlock) world.getBlockState(toCheck).getBlock();
                            if (interestingPlantBlocks.contains(cocoaBlock)) {
                                if(world.getBlockState(toCheck).has(cocoaBlock.AGE)){
                                    if(world.getBlockState(toCheck).get(cocoaBlock.AGE)<getMaxAgeForProperty(cocoaBlock.AGE)){
                                            result.add(toCheck);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public BlockPos getFloorUnderCocoa(EntityAutoFarmer farmer, BlockPos pos){
        BlockPos result = pos;
        int posX=pos.getX();
        int posY=pos.getY();
        int posZ=pos.getZ();
        World world = farmer.getEntityWorld();
        while((world.getBlockState(new BlockPos(posX,posY,posZ)).getBlock()==Blocks.AIR ||
                world.getBlockState(new BlockPos(posX,posY,posZ)).getBlock()==Blocks.COCOA ||
                world.getBlockState(new BlockPos(posX,posY,posZ)).getBlock()==Blocks.LAVA ||
                world.getBlockState(new BlockPos(posX,posY,posZ)).getBlock()==Blocks.WATER) && posY>0){
            posY--;
        }
        result = new BlockPos(posX,posY+1,posZ);
        return result;
    }

    public BlockPos getFreePosAroundCocoa(World world, BlockPos pos){
        BlockPos result=pos;
        if(world.isAirBlock(pos.south())&&world.isAirBlock(pos.south().up())){
            result=pos.south();
        }
        if(world.isAirBlock(pos.north())&&world.isAirBlock(pos.north().up())){
            result=pos.north();
        }
        if(world.isAirBlock(pos.east())&&world.isAirBlock(pos.east().up())){
            result=pos.east();
        }
        if(world.isAirBlock(pos.west())&&world.isAirBlock(pos.west().up())){
            result=pos.west();
        }
        return result;
    }

    public int getMaxAgeForProperty(Property<Integer> property){
        int result;
        Collection<Integer> allowedValues = property.getAllowedValues();
        ArrayList<Integer> allowedValuesInArrayList = new ArrayList<>(ImmutableList.copyOf(allowedValues));
        result=allowedValuesInArrayList.get(0);
        for(Integer i:allowedValuesInArrayList){
            if(i>result){
                result=i;
            }
        }
        return result;
    }

    public ArrayList<ItemWithInventoryIndexEntry> getExcessItems(EntityAutoFarmer npc){
        ArrayList<ItemWithInventoryIndexEntry> result = new ArrayList<>();
        ArrayList<Item> itemsThatFarmerWantsToKeepSomeOfThem = getItemsThatFarmerWantsToKeepUpSome(npc);

        boolean[] foundStackWithItemInInventory = new boolean[itemsThatFarmerWantsToKeepSomeOfThem.size()];

        for(int c=0;c<npc.getLocalInventory().getSizeInventory();c++){
            if(npc.getLocalInventory().getStackInSlot(c)!=null){
                if(!npc.getLocalInventory().getStackInSlot(c).isEmpty()){
                    if(npc.getLocalInventory().getStackInSlot(c).getItem()!=null){
                        if(!itemsThatFarmerWantsToKeepSomeOfThem.contains(npc.getLocalInventory().getStackInSlot(c).getItem())){
                            result.add(new ItemWithInventoryIndexEntry(npc.getLocalInventory().getStackInSlot(c).getItem(),c));
                        }else{
                            for(Item item:itemsThatFarmerWantsToKeepSomeOfThem){
                                if(npc.getLocalInventory().getStackInSlot(c).getItem()==item && !foundStackWithItemInInventory[itemsThatFarmerWantsToKeepSomeOfThem.indexOf(item)]){
                                    foundStackWithItemInInventory[itemsThatFarmerWantsToKeepSomeOfThem.indexOf(item)]=true;
                                    continue;
                                }
                                if(npc.getLocalInventory().getStackInSlot(c).getItem()==item && foundStackWithItemInInventory[itemsThatFarmerWantsToKeepSomeOfThem.indexOf(item)]){
                                    result.add(new ItemWithInventoryIndexEntry(npc.getLocalInventory().getStackInSlot(c).getItem(),c));
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<Item> getItemsThatFarmerWantsToKeepUpSome(EntityAutoFarmer farmer){
        ArrayList<Item> result = new ArrayList<>();
        ArrayList<Item> generalItemsOfInterest = farmer.getGENERAL_ALLOWED_ITEMS();
        if(generalItemsOfInterest.contains(Items.CARROT)){
            result.add(Items.CARROT);
        }
        if(generalItemsOfInterest.contains(Items.POTATO)){
            result.add(Items.POTATO);
        }
        if(generalItemsOfInterest.contains(Items.WHEAT) || generalItemsOfInterest.contains(Items.WHEAT_SEEDS)){
            result.add(Items.WHEAT_SEEDS);
        }
        if(generalItemsOfInterest.contains(Items.SUGAR_CANE)){
            result.add(Items.SUGAR_CANE);
        }
        if(generalItemsOfInterest.contains(Items.BEETROOT)){
            result.add(Items.BEETROOT_SEEDS);
        }
        if(generalItemsOfInterest.contains(Items.NETHER_WART)){
            result.add(Items.NETHER_WART);
        }
        if(generalItemsOfInterest.contains(Items.COCOA_BEANS)){
            result.add(Items.COCOA_BEANS);
        }

        //TODO make it variable?
        result.add(Items.BONE_MEAL);

        return result;
    }

    public ArrayList<Block> getPlantsThatFarmerIsInterestedAbout(EntityAutoFarmer npc){
        ArrayList<Block> result = new ArrayList<>();
        ArrayList<Item> generalItemsOfInterest = npc.getGENERAL_ALLOWED_ITEMS();
        if(generalItemsOfInterest.contains(Items.POTATO)) result.add(Blocks.POTATOES);
        if(generalItemsOfInterest.contains(Items.CARROT)) result.add(Blocks.CARROTS);
        if(generalItemsOfInterest.contains(Items.BEETROOT)) result.add(Blocks.BEETROOTS);
        if(generalItemsOfInterest.contains(Items.WHEAT) || generalItemsOfInterest.contains(Items.WHEAT_SEEDS)) result.add(Blocks.WHEAT);
        if(generalItemsOfInterest.contains(Items.SUGAR_CANE)) result.add(Blocks.SUGAR_CANE);
        if(generalItemsOfInterest.contains(Items.NETHER_WART)) result.add(Blocks.NETHER_WART);
        if(generalItemsOfInterest.contains(Items.COCOA_BEANS)) result.add(Blocks.COCOA);

        return result;
    }

    public ArrayList<BlockPos> getPlantsReadyToHarvest(EntityAutoFarmer npc){
        ArrayList<BlockPos> result=new ArrayList<>();
        ArrayList<Block> plantsThatFarmerIsInterestedAbout = getPlantsThatFarmerIsInterestedAbout(npc);
        int xRadius = npc.getWorkingRadius().getX();
        int yRadius = npc.getWorkingRadius().getY();
        int zRadius = npc.getWorkingRadius().getZ();
        World world = npc.getEntityWorld().getWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck=new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);

                    if(plantsThatFarmerIsInterestedAbout.contains(world.getBlockState(toCheck).getBlock())){
                        if(world.getBlockState(toCheck).getBlock() instanceof CropsBlock){
                            if(world.isAirBlock(toCheck.up())){
                                if(world.isAirBlock(toCheck.up(2))){
                                    CropsBlock crop = (CropsBlock) world.getBlockState(toCheck).getBlock();
                                    if(crop.isMaxAge(world.getBlockState(toCheck))){
                                        result.add(toCheck);
                                    }
                                }
                            }
                        }

                        if(world.getBlockState(toCheck).getBlock() instanceof SugarCaneBlock){
                            if(world.getBlockState(toCheck.up()).getBlock() instanceof SugarCaneBlock && !(world.getBlockState(toCheck.down()).getBlock() instanceof SugarCaneBlock)){
                                result.add(toCheck);
                            }
                        }

                        if(world.getBlockState(toCheck).getBlock() instanceof CocoaBlock){
                            CocoaBlock cocoaBlock = (CocoaBlock) world.getBlockState(toCheck).getBlock();
                            if(world.getBlockState(toCheck).has(cocoaBlock.AGE)){
                                if(world.getBlockState(toCheck).get(cocoaBlock.AGE)>=getMaxAgeForProperty(cocoaBlock.AGE)){
                                    result.add(toCheck);
                                }
                            }
                        }

                        if(world.getBlockState(toCheck).getBlock() instanceof NetherWartBlock){
                            NetherWartBlock netherWartBlock = (NetherWartBlock) world.getBlockState(toCheck).getBlock();
                            if(world.getBlockState(toCheck).has(netherWartBlock.AGE)){
                                if(world.getBlockState(toCheck).get(netherWartBlock.AGE)>=getMaxAgeForProperty(netherWartBlock.AGE)){
                                    result.add(toCheck);
                                }
                            }
                        }

                    }
                }
            }
        }
        return result;
    }

    public BlockState getBlockStateToPlant(Item item){
        BlockState result = Blocks.WHEAT.getDefaultState();
        if(item == Items.WHEAT_SEEDS) return Blocks.WHEAT.getDefaultState();
        if(item == Items.BEETROOT_SEEDS) return Blocks.BEETROOTS.getDefaultState();
        if(item == Items.CARROT) return Blocks.CARROTS.getDefaultState();
        if(item == Items.POTATO) return Blocks.POTATOES.getDefaultState();
        if(item == Items.SUGAR_CANE) return Blocks.SUGAR_CANE.getDefaultState();
        if(item == Items.COCOA_BEANS) {
            return Blocks.COCOA.getDefaultState();
        }
        if(item == Items.NETHER_WART) return Blocks.NETHER_WART.getDefaultState();
        return result;
    }

    public Item recreateSeedFromHarvestedBlock(Block block){
        Item result = Items.WHEAT_SEEDS;
        if(block == Blocks.WHEAT) return Items.WHEAT_SEEDS;
        if(block == Blocks.BEETROOTS) return Items.BEETROOT_SEEDS;
        if(block == Blocks.CARROTS) return Items.CARROT;
        if(block == Blocks.POTATOES) return Items.POTATO;
        if(block == Blocks.SUGAR_CANE) return Items.SUGAR_CANE;
        if(block == Blocks.COCOA) return Items.COCOA_BEANS;
        if(block == Blocks.NETHER_WART) return Items.NETHER_WART;
        return result;
    }

    public ArrayList<ItemWithInventoryIndexEntry> getAllowedSeedsForCropRotation(EntityAutoFarmer farmer, Item previousSeed){
        ArrayList<ItemWithInventoryIndexEntry> result = new ArrayList<>();
        ArrayList<Item> allowedSeeds = getAllowedSeeds(farmer);

        for(int c=0;c<farmer.getLocalInventory().getSizeInventory();c++){
            if(farmer.getLocalInventory().getStackInSlot(c)!=null){
                if(!farmer.getLocalInventory().getStackInSlot(c).isEmpty()){
                    if(farmer.getLocalInventory().getStackInSlot(c).getItem()!=null){
                        if(allowedSeeds.contains(farmer.getLocalInventory().getStackInSlot(c).getItem())){
                            ItemWithInventoryIndexEntry seedEntry = new ItemWithInventoryIndexEntry(farmer.getLocalInventory().getStackInSlot(c).getItem(),c);
                            result.add(seedEntry);
                        }
                    }
                }
            }
        }

        return result;
    }

    public ArrayList<Item> getAllowedSeeds(EntityAutoFarmer farmer){
        ArrayList<Item> result = new ArrayList<>();
        ArrayList<Item> generalListOfInterest = farmer.getGENERAL_ALLOWED_ITEMS();
        if(generalListOfInterest.contains(Items.WHEAT) || generalListOfInterest.contains(Items.WHEAT_SEEDS)){
            result.add(Items.WHEAT_SEEDS);
        }
        if(generalListOfInterest.contains(Items.CARROT)){
            result.add(Items.CARROT);
        }
        if(generalListOfInterest.contains(Items.POTATO)){
            result.add(Items.POTATO);
        }
        if(generalListOfInterest.contains(Items.BEETROOT) || generalListOfInterest.contains(Items.BEETROOT_SEEDS)){
            result.add(Items.BEETROOT_SEEDS);
        }
        return result;
    }

    public boolean areAnySeedsInInventory(EntityAutoFarmer npc){
            boolean result = false;
            ArrayList<Item> seedsThatAreInInterest = getAllowedSeeds(npc);

            for(int c=0;c<npc.getLocalInventory().getSizeInventory();c++){
                if(npc.getLocalInventory().getStackInSlot(c)!=null){
                    if(!npc.getLocalInventory().getStackInSlot(c).isEmpty()){
                        if(npc.getLocalInventory().getStackInSlot(c).getItem()!=null){
                            if(seedsThatAreInInterest.contains(npc.getLocalInventory().getStackInSlot(c).getItem())){
                                result=true;
                                break;
                            }
                        }
                    }
                }
            }

            return result;
    }

    public ArrayList<BlockPos> getAvailableFarmlands(EntityAutoFarmer npc){
        ArrayList<BlockPos> result=new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getX();
        int yRadius = npc.getWorkingRadius().getY();
        int zRadius = npc.getWorkingRadius().getZ();
        World world = npc.getEntityWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck=new BlockPos(npc.getHomePosition().getX()+x,npc.getHomePosition().getY()+y,npc.getHomePosition().getZ()+z);
                    if(world.getBlockState(toCheck).getBlock() == Blocks.FARMLAND){
                        if(world.getBlockState(toCheck).get(FarmlandBlock.MOISTURE)>2){
                            if(world.isAirBlock(toCheck.up())){
                                if(world.isAirBlock(toCheck.up(2))){
                                    result.add(toCheck);
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<ItemWithInventoryIndexEntry> getAllowedSeedsForFarmland(EntityAutoFarmer farmer){
        ArrayList<ItemWithInventoryIndexEntry> result = new ArrayList<>();
        ArrayList<Item> allowedSeeds = getAllowedSeeds(farmer);
        ArrayList<Item> allowedSeedsForFarmland = new ArrayList<>();
        if(allowedSeeds.contains(Items.WHEAT) || allowedSeeds.contains(Items.WHEAT_SEEDS)){
            allowedSeedsForFarmland.add(Items.WHEAT_SEEDS);
        }
        if(allowedSeeds.contains(Items.BEETROOT) || allowedSeeds.contains(Items.BEETROOT_SEEDS)){
            allowedSeedsForFarmland.add(Items.BEETROOT_SEEDS);
        }
        if(allowedSeeds.contains(Items.CARROT)){
            allowedSeedsForFarmland.add(Items.CARROT);
        }
        if(allowedSeeds.contains(Items.POTATO)){
            allowedSeedsForFarmland.add(Items.POTATO);
        }

        for(int c=0;c<farmer.getLocalInventory().getSizeInventory();c++){
            if(farmer.getLocalInventory().getStackInSlot(c)!=null){
                if(!farmer.getLocalInventory().getStackInSlot(c).isEmpty()){
                    if(farmer.getLocalInventory().getStackInSlot(c).getItem()!=null){
                        if(allowedSeedsForFarmland.contains(farmer.getLocalInventory().getStackInSlot(c).getItem())){
                            ItemWithInventoryIndexEntry seedEntry = new ItemWithInventoryIndexEntry(farmer.getLocalInventory().getStackInSlot(c).getItem(),c);
                            result.add(seedEntry);
                        }
                    }
                }
            }
        }

        return result;
    }

    public ArrayList<Block> getPlantableBlocksForSugarCane(){
        ArrayList<Block> plantableBlocks = new ArrayList<>();
        plantableBlocks.add(Blocks.GRASS);
        plantableBlocks.add(Blocks.COARSE_DIRT);
        plantableBlocks.add(Blocks.SAND);
        plantableBlocks.add(Blocks.DIRT);
        plantableBlocks.add(Blocks.PODZOL);
        plantableBlocks.add(Blocks.RED_SAND);
        return plantableBlocks;
    }

    public ArrayList<BlockPos> getAvailableFarmBlocksForSugarCane(EntityAutoFarmer npc){
        ArrayList<BlockPos> result=new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getX();
        int yRadius = npc.getWorkingRadius().getY();
        int zRadius = npc.getWorkingRadius().getZ();
        World world = npc.getEntityWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck = new BlockPos(npc.getHomePosition().getX()+x,npc.getHomePosition().getY()+y,npc.getHomePosition().getZ()+z);
                    if(world.getBlockState(toCheck).getBlock() == Blocks.WATER){
                        Block block = world.getBlockState(toCheck.west()).getBlock();
                        if(block instanceof GrassBlock || block instanceof SandBlock || block==Blocks.DIRT ||
                        block == Blocks.SAND || block == Blocks.PODZOL || block == Blocks.COARSE_DIRT || block == Blocks.RED_SAND
                         || block == Blocks.GRASS_BLOCK){
                            if(world.isAirBlock(toCheck.west().up())){
                                if(world.isAirBlock(toCheck.west().up(2))){
                                    result.add(toCheck.west().up());
                                }
                            }
                        }
                        block = world.getBlockState(toCheck.east()).getBlock();
                        if(block instanceof GrassBlock || block instanceof SandBlock || block==Blocks.DIRT ||
                                block == Blocks.SAND || block == Blocks.PODZOL || block == Blocks.COARSE_DIRT || block == Blocks.RED_SAND
                                || block == Blocks.GRASS_BLOCK){
                            if(world.isAirBlock(toCheck.east().up())){
                                if(world.isAirBlock(toCheck.east().up(2))){
                                    result.add(toCheck.east().up());
                                }
                            }
                        }
                        block = world.getBlockState(toCheck.south()).getBlock();
                        if(block instanceof GrassBlock || block instanceof SandBlock || block==Blocks.DIRT ||
                                block == Blocks.SAND || block == Blocks.PODZOL || block == Blocks.COARSE_DIRT || block == Blocks.RED_SAND
                                || block == Blocks.GRASS_BLOCK){
                            if(world.isAirBlock(toCheck.south().up())){
                                if(world.isAirBlock(toCheck.south().up(2))){
                                    result.add(toCheck.south().up());
                                }
                            }
                        }
                        block = world.getBlockState(toCheck.north()).getBlock();
                        if(block instanceof GrassBlock || block instanceof SandBlock || block==Blocks.DIRT ||
                                block == Blocks.SAND || block == Blocks.PODZOL || block == Blocks.COARSE_DIRT || block == Blocks.RED_SAND
                                || block == Blocks.GRASS_BLOCK){
                            if(world.isAirBlock(toCheck.north().up())){
                                if(world.isAirBlock(toCheck.north().up(2))){
                                    result.add(toCheck.north().up());
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public boolean isWaterAround(World world, BlockPos pos){
        boolean result = false;
        if(world.hasWater(pos.east()) || world.hasWater(pos.west()) || world.hasWater(pos.north()) || world.hasWater(pos.south())  ){
            result=true;
        }
        if(world.getFluidState(pos.east()).isTagged(FluidTags.WATER) || world.getFluidState(pos.west()).isTagged(FluidTags.WATER)
        || world.getFluidState(pos.south()).isTagged(FluidTags.WATER) || world.getFluidState(pos.north()).isTagged(FluidTags.WATER)){
            result=true;
        }
        if(world.getBlockState(pos.east()).getBlock() == Blocks.WATER || world.getBlockState(pos.west()).getBlock() == Blocks.WATER ||
                world.getBlockState(pos.south()).getBlock() == Blocks.WATER || world.getBlockState(pos.north()).getBlock() == Blocks.WATER){
            result=true;
        }
        return result;
    }

    public ArrayList<BlockPos> getAvailableSoulBlocksForNetherWarts(EntityAutoFarmer npc){
        ArrayList<BlockPos> result=new ArrayList<>();

        int xRadius = npc.getWorkingRadius().getX();
        int yRadius = npc.getWorkingRadius().getY();
        int zRadius = npc.getWorkingRadius().getZ();
        World world = npc.getEntityWorld().getWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck=new BlockPos(npc.getHomePosition().getX()+x,npc.getHomePosition().getY()+y,npc.getHomePosition().getZ()+z);
                    if(world.getBlockState(toCheck).getBlock() == Blocks.SOUL_SAND){
                            if(world.isAirBlock(toCheck.up())){
                                if(world.isAirBlock(toCheck.up(2))){
                                    result.add(toCheck);
                                }
                            }
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<BlockPos> getAvailableLogsForCocoaBean(EntityAutoFarmer npc){
        ArrayList<BlockPos> result=new ArrayList<>();

        int xRadius = npc.getWorkingRadius().getX();
        int yRadius = npc.getWorkingRadius().getY();
        int zRadius = npc.getWorkingRadius().getZ();
        World world = npc.getEntityWorld().getWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck=new BlockPos(npc.getHomePosition().getX()+x,npc.getHomePosition().getY()+y,npc.getHomePosition().getZ()+z);
                    if(world.getBlockState(toCheck).getBlock() == Blocks.JUNGLE_LOG){
                        if(isAirAround(world,toCheck)){
                            result.add(toCheck);
                        }
                    }
                }
            }
        }
        return result;
    }

    public boolean isAirAround(World world, BlockPos pos){
        boolean result = false;

        if(world.isAirBlock(pos.east())) result=true;
        if(world.isAirBlock(pos.west())) result=true;
        if(world.isAirBlock(pos.south())) result=true;
        if(world.isAirBlock(pos.north())) result=true;


        return result;
    }

    public BlockPos getAirBlockAround(World world, BlockPos pos){
        BlockPos result = pos;

        if(world.isAirBlock(pos.east())) result=pos.east();
        if(world.isAirBlock(pos.west())) result=pos.west();
        if(world.isAirBlock(pos.south())) result=pos.south();
        if(world.isAirBlock(pos.north())) result=pos.north();


        return result;
    }

}
