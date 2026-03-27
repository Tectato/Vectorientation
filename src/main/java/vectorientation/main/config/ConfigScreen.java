package vectorientation.main.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import vectorientation.main.Vectorientation;
import vectorientation.main.ui.StringListWidget;

public class ConfigScreen extends Screen {

    private Button backButton;
    private Checkbox toggleSquish;//, toggleMinecarts;
    private EditBox squishMinInput, squishFactorInput;
    private StringListWidget blacklist;
    private Screen parent;

    protected ConfigScreen(Component title) {
        super(title);
        setup();
    }

    public ConfigScreen(Screen parent, Minecraft client, int width, int height) {
        super(Component.nullToEmpty("Vectorientation Config"));
        this.parent = parent;
        init(client, width, height);
        setup();
    }

    @Override
    public void onClose() {
        if (parent != null && minecraft != null) {
            if(blacklist.getChanged()){
                Vectorientation.setConfig(Vectorientation.VAR_BLACKLIST, blacklist.getList());
            }
            Vectorientation.writeConfig();
            minecraft.setScreen(parent);
        }
    }

    private void setup(){
        int posX = width / 2;
        int posY = 32;

        if (parent != null ) {
            backButton = Button.builder(CommonComponents.GUI_BACK, button -> onClose()).bounds(8,8,50,20).build();
        }
        addRenderableWidget(backButton);
        /*toggleMinecarts = CheckboxWidget.builder(Text.of(""), textRenderer)
                .pos(posX, posY)
                .checked(Vectorientation.MINECARTS)
                .tooltip(Tooltip.of(Text.of("Warning: Very janky!")))
                .callback(new CheckboxWidget.Callback() {
                    @Override
                    public void onValueChange(CheckboxWidget checkbox, boolean checked) {
                        Vectorientation.setConfig(Vectorientation.VAR_MINECARTS, ""+checked);
                    }
                })
                .build();
        posY += 24;*/
        toggleSquish = Checkbox.builder(Component.nullToEmpty(""), font)
                .pos(posX, posY)
                .selected(Vectorientation.SQUETCH)
                .onValueChange(new Checkbox.OnValueChange() {
                    @Override
                    public void onValueChange(Checkbox checkbox, boolean checked) {
                        Vectorientation.setConfig(Vectorientation.VAR_SQUETCH, ""+checked);
                    }
                })
                .build();
        posY += 24;
        squishMinInput = new EditBox(font, posX, posY, 64, 12, Component.nullToEmpty(""));
        squishMinInput.setValue(""+Vectorientation.MIN_WARP);
        squishMinInput.setResponder((string)->{
            try {
                double inputDouble = Math.min(Math.max(Double.parseDouble(string),-8.0),8.0);
                Vectorientation.setConfig(Vectorientation.VAR_MIN_WARP, String.valueOf(inputDouble));
            } catch (NumberFormatException e){
                Vectorientation.MIN_WARP = 0.75d;
            }
        });
        posY += 16;
        squishFactorInput = new EditBox(font, posX, posY, 64, 12, Component.nullToEmpty(""));
        squishFactorInput.setValue(""+Vectorientation.WARP_FACTOR);
        squishFactorInput.setResponder((string)->{
            try {
                double inputDouble = Math.min(Math.max(Double.parseDouble(string),-8.0),8.0);
                Vectorientation.setConfig(Vectorientation.VAR_WARP_FACTOR, String.valueOf(inputDouble));
            } catch (NumberFormatException e){
                Vectorientation.WARP_FACTOR = 1.0d;
            }
        });
        posY += 30;
        blacklist = new StringListWidget(font, 4, posY, width/2, height - (posY + 4), Component.nullToEmpty(""));
        blacklist.setEntries(Vectorientation.BLACKLIST);
        addRenderableWidget(blacklist);

        //addDrawableChild(toggleMinecarts);
        addRenderableWidget(toggleSquish);
        addRenderableWidget(squishMinInput);
        addRenderableWidget(squishFactorInput);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        //renderBackground(context, mouseX, mouseY, delta);
        backButton.render(context, mouseX, mouseY, delta);
        //context.drawTextWithShadow(textRenderer, "Affect Minecarts:", 8, toggleMinecarts.getY() + 4, 0xFFFFFFFF);
        //toggleMinecarts.render(context, mouseX, mouseY, delta);
        context.drawString(font, "Enable Squash & stretch:", 8, toggleSquish.getY() + 4, 0xFFFFFFFF);
        toggleSquish.render(context, mouseX, mouseY, delta);
        context.drawString(font, "Vertical squish at 0 velocity:", 8, squishMinInput.getY(), 0xFFFFFFFF);
        squishMinInput.render(context, mouseX, mouseY, delta);
        context.drawString(font, "Amount of squish increase with velocity:", 8, squishFactorInput.getY(), 0xFFFFFFFF);
        squishFactorInput.render(context, mouseX, mouseY, delta);
        context.drawString(font, "Blocks that should not squish:", 8, blacklist.getY() - 12, 0xFFFFFFFF);
        blacklist.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean isDoubled){
        if(backButton.mouseClicked(click, isDoubled)) return true;
        //if(toggleMinecarts.mouseClicked(mouseX, mouseY, button)) return true;
        if(toggleSquish.mouseClicked(click, isDoubled)) return true;
        if(squishMinInput.mouseClicked(click, isDoubled)) {
            setFocused(squishMinInput);
            return true;
        }
        if(squishFactorInput.mouseClicked(click, isDoubled)) {
            setFocused(squishFactorInput);
            return true;
        }
        if(blacklist.mouseClicked(click, isDoubled)){
            setFocused(blacklist);
            return true;
        }
        return super.mouseClicked(click, isDoubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(blacklist.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        if(blacklist.charTyped(input)) return true;
        return super.charTyped(input);
    }
}
