package vectorientation.main.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.LinkedList;

public class StringListWidget extends AbstractWidget {

    private class ListEntry{
        private static final int height = 18;
        protected int x, y, width;
        protected int index;
        protected EditBox content;
        protected Button removeButton;
        protected StringListWidget parent;

        public ListEntry(StringListWidget parent, Font textRenderer, ListEntry previous, String text, java.util.function.Consumer<Integer> changedListener) {
            this(parent, textRenderer, previous.x, previous.y + height, previous.width, previous.index + 1, text, changedListener);
        }

        public ListEntry(StringListWidget parent, Font textRenderer, int x, int y, int width, int index, String text, java.util.function.Consumer<Integer> changedListener){
            this.parent = parent;
            this.index = index;
            this.x = x;
            this.y = y;
            this.width = width;
            content = new EditBox(textRenderer, x, y, width - 16, height, Component.nullToEmpty(""));
            content.setValue(text);
            content.setResponder(s -> {textChanged();});

            removeButton = Button.builder(Component.nullToEmpty("[-]"), button -> this.parent.delete(this.index)).bounds(x + width - 15, y, 30, height - 2).build();
        }

        public void setIndex(int index){
            this.index = index;
        }

        public void setY(int y){
            this.y = y;
            content.setY(y);
            removeButton.setY(y);
        }

        public void textChanged(){
            parent.onEntryChanged();
        }

        public String getText(){
            return content.getValue();
        }

        public void render(GuiGraphics context, int mouseX, int mouseY, float delta){
            content.render(context, mouseX, mouseY, delta);
            removeButton.render(context, mouseX, mouseY, delta);
        }

        public boolean mouseClicked(MouseButtonEvent click, boolean isDoubled){
            if (removeButton.mouseClicked(click, isDoubled)) return true;
            content.mouseClicked(click, isDoubled);
            boolean hoveringTextField =
                    (click.x() >= content.getX())
                    && (click.x() < content.getX() + content.getWidth())
                    && (click.y() >= content.getY())
                    && (click.y() < content.getY() + content.getHeight());
            content.setFocused(hoveringTextField);
            return hoveringTextField;
        }
        public boolean charTyped(CharacterEvent charInput){
            return content.charTyped(charInput);
        }

        public boolean keyPressed(KeyEvent keyInput) {
            return content.keyPressed(keyInput);
        }
    }

    private LinkedList<ListEntry> entries; // Feels wrong to do this, but oh well...
    private Button addButton;
    private InteractiveScrollableWidget slider;
    private Font textRenderer;
    private boolean changed = false;

    public StringListWidget(Font textRenderer, int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.textRenderer = textRenderer;
        entries = new LinkedList<>();
        addButton = Button.builder(Component.nullToEmpty("[+]"), button -> add("")).bounds(x + 2, y + height - 17, 30, 16).build();
        slider = new InteractiveScrollableWidget(x , y, width, height, Component.nullToEmpty("")) {

            @Override
            protected void updateWidgetNarration(NarrationElementOutput builder) {

            }

            @Override
            protected int contentHeight() {
                return entries.size() * ListEntry.height + addButton.getHeight() + 4;
            }

            @Override
            protected double scrollRate() {
                return ListEntry.height;
            }

            @Override
            protected void renderContents(GuiGraphics context, int mouseX, int mouseY, float delta) {
                for(ListEntry entry : entries){
                    entry.render(context, mouseX, mouseY, delta);
                }
                addButton.render(context, mouseX, mouseY, delta);
            }
        };
        slider.setChangedListener((value)->{
            value *= -1;
            for(int i=0; i<entries.size(); i++){
                entries.get(i).setY((int) (getY() + value + 2 + i * ListEntry.height));
            }
            addButton.setY((int) (getY() + value + 3 + entries.size() * ListEntry.height));
        });
    }

    public void setEntries(String list){
        String[] textEntries = list.replace(" ","").split(",");
        for(String entry : textEntries){
            ResourceLocation identifier = ResourceLocation.parse(entry);
            if(BuiltInRegistries.BLOCK.containsKey(identifier)){
                add(entry);
            }
        }
    }

    public void setEntries(HashSet<ResourceLocation> set){
        for(ResourceLocation block : set){
            add(block.toString().replace(block.getNamespace()+":", ""));
        }
    }

    public void add(String text){
        ListEntry newEntry;
        if(entries.isEmpty()){
            newEntry = new ListEntry(this, textRenderer, getX() + 2, getY() + 2, width - 24, 0, text, (String) -> changed = true);
        } else {
            newEntry = new ListEntry(this, textRenderer, entries.getLast(), text, (String) -> changed = true);
        }
        entries.add(newEntry);
        addButton.setY((int) (getY() + (-slider.scrollAmount()) + 3 + entries.size() * ListEntry.height));
    }

    public void delete(int index){
        entries.remove(index);
        for(int i=index; i<entries.size(); i++){
            entries.get(i).setIndex(i);
            entries.get(i).setY((int) (getY() + (-slider.scrollAmount()) + 2 + i * ListEntry.height));
        }
        addButton.setY((int) (getY() + (-slider.scrollAmount()) + 3 + entries.size() * ListEntry.height));
        onEntryChanged();
    }

    public void onEntryChanged(){
        changed = true;
    }

    public boolean getChanged(){
        return changed;
    }

    public String getList(){
        StringBuilder builder = new StringBuilder();

        for (ListEntry entry : entries){
            String entryText = entry.getText().replace(",","");
            builder.append(entryText + ",");
        }

        return builder.substring(0,Math.max(0,builder.length()-1));
    }

    public void setScroll(double value){
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        slider.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean isDoubled){
        boolean entryClicked = false;
        int listCount = entries.size();
        for(ListEntry entry : entries){
            if(entry.mouseClicked(click, isDoubled)) entryClicked = true;
            if(entries.size() < listCount) return true;
        }
        if(entryClicked) return true;
        if(addButton.mouseClicked(click, isDoubled)) return true;
        if(slider.mouseClicked(click, isDoubled)){
            slider.onClick(click, isDoubled);
            return true;
        }
        return super.mouseClicked(click, isDoubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(slider.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        for(ListEntry entry : entries){
            if(entry.charTyped(input)) return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent keyInput) {
        for(ListEntry entry : entries){
            if(entry.keyPressed(keyInput)) return true;
        }
        return false;
    }
}
