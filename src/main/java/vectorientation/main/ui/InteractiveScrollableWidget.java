package vectorientation.main.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;

public abstract class InteractiveScrollableWidget extends ScrollableWidget {

    private java.util.function.Consumer<Double> changedListener;

    public InteractiveScrollableWidget(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    public void setChangedListener(java.util.function.Consumer<Double> changedListener){
        this.changedListener = changedListener;
    }

    @Override
    public void setScrollY(double scrollY) {
        super.setScrollY(scrollY);
        changedListener.accept(getScrollY());
    }

    public double getScrollY(){
        return super.getScrollY();
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            context.drawStrokedRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight(), Colors.WHITE);
            context.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
            this.renderContents(context, mouseX, mouseY, delta);
            context.disableScissor();
            this.drawScrollbar(context, mouseX, mouseY);
        }
    }


    @Override
    protected abstract int getContentsHeightWithPadding();
    @Override
    protected abstract double getDeltaYPerScroll();

    protected abstract void renderContents(DrawContext context, int mouseX, int mouseY, float delta);
    @Override
    protected abstract void appendClickableNarrations(NarrationMessageBuilder builder);
}
