package krevik.github.io.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.init.ModItems;
import krevik.github.io.networking.PacketsHandler;
import krevik.github.io.networking.messages.farmer.ServerUpdateFarmerGeneralAllowedItems;
import krevik.github.io.util.ModReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;


public class GuiFarmer extends Screen {
    private EntityAutoFarmer farmer;
    private PlayerEntity player;
    private static ArrayList<Item> allowedItems;
    private ArrayList<ToggleSwitchButton> buttons;
    private Button updateButton;
    private ItemStack[] stacksToAllow;
    private TextFieldWidget work_speed;
    private int updateCount;
    private int workSpeedInfoFromFarmer;
    private TextFieldWidget working_radius_x;
    private TextFieldWidget working_radius_y;
    private TextFieldWidget working_radius_z;
    private BlockPos working_Radius_Info_From_Farmer;

    public GuiFarmer(PlayerEntity player, EntityAutoFarmer farmer, int workSpeed, BlockPos workingRadius, ItemStack... stacks) {
        super(NarratorChatListener.EMPTY);
        this.player=player;
        this.farmer=farmer;
        this.stacksToAllow = stacks;
        this.workSpeedInfoFromFarmer=workSpeed;
        this.working_Radius_Info_From_Farmer=workingRadius;
    }


    @Override
    protected void init() {
        super.init();
        allowedItems = new ArrayList<>();
        buttons = new ArrayList<>();
        loadAllowedItems();
        int actualX = (int) (width/2-24*2.5f);
        int actualY = height/4;
        int shiftX = 24;
        int shiftZ = 24;
        //creates gui
        for(int i=0;i<allowedItems.size();i++){
            ToggleSwitchButton button = new ToggleSwitchButton(actualX+shiftX*i-shiftX*5*MathHelper.floor(i/5),actualY+shiftZ*MathHelper.floor(i/5),16,16, "button_farmer_gui_"+i,allowedItems.get(i));
            buttons.add(button);
            addButton(button);
        }

        updateButton = new Button(width/2-50,200,100,20, I18n.format("farmer.gui.update"),(p_214212_1_) -> {
            ArrayList<ItemStack> finalItemsToAllow = new ArrayList<>();
            for(Button button:buttons){
                if(button instanceof ToggleSwitchButton){
                    ToggleSwitchButton toggleSwitchButton = (ToggleSwitchButton) button;
                    if(toggleSwitchButton.getIsDown()){
                        if(toggleSwitchButton.getItem()!=Items.BONE_MEAL && toggleSwitchButton.getItem()!=ModItems.MULTI_CROP){
                            finalItemsToAllow.add(new ItemStack(toggleSwitchButton.getItem()));
                        }
                    }
                }
            }
            boolean shouldUseBonemeal=false;
            boolean shouldUseCropRotation=false;
            for(ToggleSwitchButton button: buttons){
                if(button.getItem()==Items.BONE_MEAL){
                    if(button.getIsDown()){
                        shouldUseBonemeal=true;
                        break;
                    }
                }
            }

            for(ToggleSwitchButton button: buttons){
                if(button.getItem()==ModItems.MULTI_CROP){
                    if(button.getIsDown()){
                        shouldUseCropRotation=true;
                        break;
                    }
                }
            }

            int work_speed_value=tryToReadTextIntoInt(1,10,work_speed);
            int finalWorkingRadiusX=tryToReadTextIntoInt(1,100,working_radius_x);
            int finalWorkingRadiusY=tryToReadTextIntoInt(1,100,working_radius_y);
            int finalWorkingRadiusZ=tryToReadTextIntoInt(1,100,working_radius_z);

            BlockPos finalWorkingRadius = new BlockPos(finalWorkingRadiusX,finalWorkingRadiusY,finalWorkingRadiusZ);

            PacketsHandler.sendToServer(new ServerUpdateFarmerGeneralAllowedItems(farmer.getPosition(),work_speed_value,finalWorkingRadius,finalItemsToAllow.size(),finalItemsToAllow,shouldUseBonemeal,shouldUseCropRotation));
            this.minecraft.displayGuiScreen(null);
        });
        addButton(updateButton);

        this.work_speed = new TextFieldWidget(this.font, this.width / 2 - 40, 20, 80, 20, I18n.format("farmer.gui.work_speed"));
        this.work_speed.setMaxStringLength(2);
        this.work_speed.setText(String.valueOf(workSpeedInfoFromFarmer));
        this.children.add(this.work_speed);

        this.working_radius_x = new TextFieldWidget(this.font, this.width / 2 - 130, 150, 80, 20, I18n.format("farmer.gui.working.radius.x"));
        this.working_radius_x.setMaxStringLength(3);
        this.working_radius_x.setText(String.valueOf(working_Radius_Info_From_Farmer.getX()));
        this.children.add(this.working_radius_x);

        this.working_radius_y = new TextFieldWidget(this.font, this.width / 2 - 40, 150, 80, 20, I18n.format("farmer.gui.working.radius.y"));
        this.working_radius_y.setMaxStringLength(3);
        this.working_radius_y.setText(String.valueOf(working_Radius_Info_From_Farmer.getY()));
        this.children.add(this.working_radius_y);

        this.working_radius_z = new TextFieldWidget(this.font, this.width / 2 + 50, 150, 80, 20, I18n.format("farmer.gui.working.radius.z"));
        this.working_radius_z.setMaxStringLength(3);
        this.working_radius_z.setText(String.valueOf(working_Radius_Info_From_Farmer.getZ()));
        this.children.add(this.working_radius_z);


        //updates buttons basing on farmer
        updateAllowedItemsFromFarmer();
    }

    private boolean isNumeric(String text){
        boolean result;
        result = text.matches("\\d+(\\.\\d+)?");
        if(text.contains(".")||text.contains(",")){
            result=false;
        }
        return result;
    }

    private int tryToReadTextIntoInt(int min, int max,TextFieldWidget widget){
        int result=1;
        if(widget!=null){
            if(widget.getText()!=null){
                String widgetText = widget.getText();
                if(isNumeric(widgetText)){
                    int i = Integer.parseInt(widgetText);
                    result=i;
                    if(result>max){
                        result=max;
                        widget.setText(String.valueOf(max));
                    }
                    if(result<min){
                        result=min;
                        widget.setText(String.valueOf(min));
                    }
                }else{
                    widget.setText(String.valueOf(min));
                    result=min;
                }
            }
        }
        return result;
    }

    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        String s1 = this.working_radius_x.getText();
        String s2 = this.working_radius_y.getText();
        String s3 = this.working_radius_z.getText();
        String s4 = this.work_speed.getText();
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
        this.work_speed.setText(s4);
        this.working_radius_x.setText(s1);
        this.working_radius_y.setText(s2);
        this.working_radius_z.setText(s3);
    }

    private void updateAllowedItemsFromFarmer(){
        ArrayList<Item> itemsToAllow = new ArrayList<>();
        for(ItemStack stack:stacksToAllow){
            itemsToAllow.add(stack.getItem());
        }
        for(ToggleSwitchButton button: buttons){
            if(itemsToAllow.contains(button.getItem())){
                button.setIsDown(true);
            }
        }
    }

    private void loadAllowedItems(){
        allowedItems.add(Items.WHEAT);
        allowedItems.add(Items.BEETROOT);
        allowedItems.add(Items.CARROT);
        allowedItems.add(Items.POTATO);
        allowedItems.add(Items.SUGAR_CANE);
        allowedItems.add(Items.NETHER_WART);
        allowedItems.add(Items.COCOA_BEANS);
        allowedItems.add(Items.BONE_MEAL);
        allowedItems.add(ModItems.MULTI_CROP);
    }


    @Override
    public void tick() {
        super.tick();
        ++this.updateCount;
        this.work_speed.tick();
        this.working_radius_x.tick();
        this.working_radius_y.tick();
        this.working_radius_z.tick();
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 10, 16777215);
        this.drawString(this.font, I18n.format(I18n.format("farmer.gui.work_speed")), this.width / 2 - 40 - 30, 10, 10526880);
        this.drawString(this.font, I18n.format(I18n.format("farmer.gui.working.radius.x")), this.width / 2 - 130, 130, 10526880);
        this.drawString(this.font, I18n.format(I18n.format("farmer.gui.working.radius.y")), this.width / 2 - 40, 130, 10526880);
        this.drawString(this.font, I18n.format(I18n.format("farmer.gui.working.radius.z")), this.width / 2 + 50, 130, 10526880);

        this.work_speed.render(p_render_1_, p_render_2_, p_render_3_);
        this.working_radius_x.render(p_render_1_, p_render_2_, p_render_3_);
        this.working_radius_y.render(p_render_1_, p_render_2_, p_render_3_);
        this.working_radius_z.render(p_render_1_, p_render_2_, p_render_3_);

        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public void removed() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }


    @Override
    public void onClose() {
        this.minecraft.displayGuiScreen((Screen)null);;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public class ToggleSwitchButton extends Button{
        public final ResourceLocation BUTTON_TRUE = new ResourceLocation(ModReference.MOD_ID,"textures/gui/button_true.png");
        public final ResourceLocation BUTTON_FALSE = new ResourceLocation(ModReference.MOD_ID,"textures/gui/button_false.png");

        private boolean isDown=false;
        private Item item;
        public ToggleSwitchButton(int widthIn, int heightIn, int width, int height, String text, Item item) {
            super(widthIn, heightIn, width, height, text, null);
            this.item=item;
        }

        @Override
        public void renderToolTip(int x, int z) {
            super.renderToolTip(x, z);
        }

        @Override
        public void onPress() {
            if(!isDown){
                isDown=true;
            }else{
                isDown=false;
            }
        }

        public Item getItem(){
            return item;
        }

        public void setIsDown(boolean bool){
            isDown = bool;
        }

        public boolean getIsDown(){
            return isDown;
        }

        @Override
        public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
            Minecraft minecraft = Minecraft.getInstance();
            if(isDown){
                minecraft.getTextureManager().bindTexture(BUTTON_TRUE);
            }else{
                minecraft.getTextureManager().bindTexture(BUTTON_FALSE);
            }
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
            int i = this.getYImage(this.isHovered());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos((double)this.x, (double)this.y+16, (double)0).tex(0, 1).endVertex();
            bufferbuilder.pos((double)this.x+16, (double)this.y+16, (double)0).tex(1, 1).endVertex();
            bufferbuilder.pos((double)this.x+16, (double)this.y+0, (double)0).tex(1, 0).endVertex();
            bufferbuilder.pos((double)this.x+0, (double)this.y+0, (double)0).tex(0, 0).endVertex();
            bufferbuilder.finishDrawing();
            RenderSystem.enableAlphaTest();
            WorldVertexBufferUploader.draw(bufferbuilder);
            this.renderBg(minecraft, p_renderButton_1_, p_renderButton_2_);
            Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(new ItemStack(getItem()),this.x,this.y);

        }
    }

}
