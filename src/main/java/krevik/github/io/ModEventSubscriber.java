package krevik.github.io;

import krevik.github.io.block.BlockAutoFarmerSpawner;
import krevik.github.io.block.BlockAutoLumberjackSpawner;
import krevik.github.io.block.BlockFishermanSpawner;
import krevik.github.io.entity.*;
import krevik.github.io.init.ModEntities;
import krevik.github.io.util.ModReference;
import krevik.github.io.util.ModUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

/**
 * @author Krevik
 */
@Mod.EventBusSubscriber(modid = ModReference.MOD_ID, bus = MOD)
public final class ModEventSubscriber {

    @SubscribeEvent
    public static void onRegisterEntityTypes(final RegistryEvent.Register<EntityType<?>> event){
        event.getRegistry().registerAll(
                setup(ModEntities.ENTITY_AUTO_FARMER=EntityType.Builder.<EntityAutoFarmer>create(EntityAutoFarmer::new, EntityClassification.AMBIENT).size(0.6F,1.95F).build(ModReference.MOD_ID+":"+"auto_farmer"),"auto_farmer"),
                setup(ModEntities.ENTITY_AUTO_LUMBERJACK=EntityType.Builder.<EntityAutoLumberjack>create(EntityAutoLumberjack::new, EntityClassification.AMBIENT).size(0.6F,1.95F).build(ModReference.MOD_ID+":"+"auto_lumberjack"),"auto_lumberjack"),
                setup(ModEntities.ENTITY_MINER=EntityType.Builder.<EntityMiner>create(EntityMiner::new, EntityClassification.AMBIENT).size(0.6F,1.95F).build(ModReference.MOD_ID+":"+"miner"),"miner"),
                setup(ModEntities.ENTITY_FISHERMAN=EntityType.Builder.<EntityFisherman>create(EntityFisherman::new, EntityClassification.AMBIENT).size(0.6F,1.95F).build(ModReference.MOD_ID+":"+"fisherman"),"fisherman"),
                setup(ModEntities.CUSTOM_FISHING_BOBBER=EntityType.Builder.<EntityCustomFishingBobber>create(EntityCustomFishingBobber::new, EntityClassification.MISC).disableSerialization().disableSummoning().size(0.25F, 0.25F).setCustomClientFactory(EntityCustomFishingBobber::new).build(ModReference.MOD_ID+":"+"custom_fishing_bobber"),"custom_fishing_bobber")
        );
    }


    @SubscribeEvent
    public static void onRegisterBlocks(final RegistryEvent.Register<Block> event){
        event.getRegistry().registerAll(
                setup(new BlockAutoFarmerSpawner(Block.Properties.create(Material.WOOD).hardnessAndResistance(1f).sound(SoundType.WOOD)),"auto_farmer_spawner"),
                setup(new BlockAutoLumberjackSpawner(Block.Properties.create(Material.WOOD).hardnessAndResistance(1f).sound(SoundType.WOOD)),"auto_lumberjack_spawner"),
                setup(new BlockFishermanSpawner(Block.Properties.create(Material.WOOD).hardnessAndResistance(1f).sound(SoundType.WOOD)),"fisherman_spawner")
                //setup(new Block(Block.Properties.create(Material.WOOD).hardnessAndResistance(1f).sound(SoundType.WOOD)),"auto_farmer_spawner")
        );
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event){
        final IForgeRegistry<Item> registry = event.getRegistry();
        event.getRegistry().registerAll(
                setup(new Item(new Item.Properties()), "multi_crop"),
                setup(new Item(new Item.Properties()), "multi_sapling")
        );
        for (final Block block : ModUtil.getModEntries(ForgeRegistries.BLOCKS)) {
            registry.register(setup(new BlockItem(block, new Item.Properties().group(ItemGroup.REDSTONE)), block.getRegistryName()));
        }
    }

    public static <T extends IForgeRegistryEntry> T setup(final T entry, final String name) {
        return setup(entry, new ResourceLocation(ModReference.MOD_ID, name));
    }

    public static <T extends IForgeRegistryEntry> T setup(final T entry, final ResourceLocation registryName) {
        entry.setRegistryName(registryName);
        return entry;
    }
}
