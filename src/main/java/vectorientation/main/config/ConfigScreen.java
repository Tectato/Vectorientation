package vectorientation.main.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import vectorientation.main.Vectorientation;
import vectorientation.main.ui.StringListWidget;

public class ConfigScreen extends Screen {

    private ButtonWidget backButton;
    private CheckboxWidget toggleSquish;//, toggleMinecarts;
    private TextFieldWidget squishMinInput, squishFactorInput;
    private StringListWidget blacklist;
    private Screen parent;

    protected ConfigScreen(Text title) {
        super(title);
        setup();
    }

    public ConfigScreen(Screen parent, MinecraftClient client, int width, int height) {
        super(Text.of("Vectorientation Config"));
        this.parent = parent;
        init(client, width, height);
        setup();
    }

    @Override
    public void close() {
        if (parent != null && client != null) {
            if(blacklist.getChanged()){
                Vectorientation.setConfig(Vectorientation.VAR_BLACKLIST, blacklist.getList());
            }
            Vectorientation.writeConfig();
            client.setScreen(parent);
        }
    }

    private void setup(){
        int posX = width / 2;
        int posY = 32;

        if (parent != null ) {
            backButton = ButtonWidget.builder(ScreenTexts.BACK, button -> close()).dimensions(8,8,50,20).build();
        }
        addDrawableChild(backButton);
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
        toggleSquish = CheckboxWidget.builder(Text.of(""), textRenderer)
                .pos(posX, posY)
                .checked(Vectorientation.SQUETCH)
                .callback(new CheckboxWidget.Callback() {
                    @Override
                    public void onValueChange(CheckboxWidget checkbox, boolean checked) {
                        Vectorientation.setConfig(Vectorientation.VAR_SQUETCH, ""+checked);
                    }
                })
                .build();
        posY += 24;
        squishMinInput = new TextFieldWidget(textRenderer, posX, posY, 64, 12, Text.of(""));
        squishMinInput.setText(""+Vectorientation.MIN_WARP);
        squishMinInput.setChangedListener((string)->{
            try {
                double inputDouble = Math.min(Math.max(Double.parseDouble(string),-8.0),8.0);
                Vectorientation.setConfig(Vectorientation.VAR_MIN_WARP, String.valueOf(inputDouble));
            } catch (NumberFormatException e){
                Vectorientation.MIN_WARP = 0.75d;
            }
        });
        posY += 16;
        squishFactorInput = new TextFieldWidget(textRenderer, posX, posY, 64, 12, Text.of(""));
        squishFactorInput.setText(""+Vectorientation.WARP_FACTOR);
        squishFactorInput.setChangedListener((string)->{
            try {
                double inputDouble = Math.min(Math.max(Double.parseDouble(string),-8.0),8.0);
                Vectorientation.setConfig(Vectorientation.VAR_WARP_FACTOR, String.valueOf(inputDouble));
            } catch (NumberFormatException e){
                Vectorientation.WARP_FACTOR = 1.0d;
            }
        });
        posY += 30;
        blacklist = new StringListWidget(textRenderer, 4, posY, width/2, height - (posY + 4), Text.of(""));
        blacklist.setEntries(Vectorientation.BLACKLIST);
        addDrawableChild(blacklist);

        //addDrawableChild(toggleMinecarts);
        addDrawableChild(toggleSquish);
        addDrawableChild(squishMinInput);
        addDrawableChild(squishFactorInput);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        //renderBackground(context, mouseX, mouseY, delta);
        backButton.render(context, mouseX, mouseY, delta);
        //context.drawTextWithShadow(textRenderer, "Affect Minecarts:", 8, toggleMinecarts.getY() + 4, 0xFFFFFFFF);
        //toggleMinecarts.render(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(textRenderer, "Enable Squash & stretch:", 8, toggleSquish.getY() + 4, 0xFFFFFFFF);
        toggleSquish.render(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(textRenderer, "Vertical squish at 0 velocity:", 8, squishMinInput.getY(), 0xFFFFFFFF);
        squishMinInput.render(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(textRenderer, "Amount of squish increase with velocity:", 8, squishFactorInput.getY(), 0xFFFFFFFF);
        squishFactorInput.render(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(textRenderer, "Blocks that should not squish:", 8, blacklist.getY() - 12, 0xFFFFFFFF);
        blacklist.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean isDoubled){
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
    public boolean charTyped(CharInput input) {
        if(blacklist.charTyped(input)) return true;
        return super.charTyped(input);
    }
}
