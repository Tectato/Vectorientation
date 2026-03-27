package vectorientation.main.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;

public abstract class InteractiveScrollableWidget extends AbstractScrollArea {

    private java.util.function.Consumer<Double> changedListener;

    public InteractiveScrollableWidget(int i, int j, int k, int l, Component text, final ScrollbarSettings scrollbarSettings) {
        super(i, j, k, l, text, scrollbarSettings);
    }

    public void setChangedListener(java.util.function.Consumer<Double> changedListener){
        this.changedListener = changedListener;
    }

    @Override
    public void setScrollAmount(double scrollY) {
        super.setScrollAmount(scrollY);
        changedListener.accept(scrollAmount());
    }

    public double scrollAmount(){
        return super.scrollAmount();
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            graphics.outline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), CommonColors.WHITE);
            graphics.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
            this.extractContents(graphics, mouseX, mouseY, delta);
            graphics.disableScissor();
            this.extractScrollbar(graphics, mouseX, mouseY);
        }
    }


    @Override
    protected abstract int contentHeight();
    @Override
    protected abstract double scrollRate();

    protected abstract void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta);
    @Override
    protected abstract void updateWidgetNarration(NarrationElementOutput builder);
}
