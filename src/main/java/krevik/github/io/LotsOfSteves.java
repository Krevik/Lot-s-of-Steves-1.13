package krevik.github.io;

import krevik.github.io.util.FunctionHelper;
import krevik.github.io.util.ModReference;
import krevik.github.io.util.RenderersRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Krevik
 */
@Mod(ModReference.MOD_ID)
public final class LotsOfSteves {


    public static final Logger LOG = LogManager.getLogger(ModReference.MOD_ID);

    public LotsOfSteves() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverStarting);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void loadComplete(final FMLLoadCompleteEvent event){

    }

    private void serverStarting(final FMLServerStartingEvent event){
    }


    private void setupClient(final FMLClientSetupEvent event) {
        RenderersRegistry.registerRenders();
    }

    public static FunctionHelper getHelper(){
        return new FunctionHelper();
    }

}
