package vectorientation.main;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.Minecraft;
import vectorientation.main.config.ConfigScreen;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory(){
        return parent -> new ConfigScreen(parent, Minecraft.getInstance(), parent.width, parent.height);
    }
}
