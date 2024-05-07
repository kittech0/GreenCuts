package dev.venomcode.greencuts;

import dev.venomcode.serverapi.api.ServerAPI;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;

import static com.mojang.text2speech.Narrator.LOGGER;

public class GreenCuts implements ModInitializer {
    public static final String MODID = "greencuts";
    private static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize( )
    {
        GreenCutsConfig tmp = getConfig();
        saveConfig();
    }


    public static GreenCutsConfig getConfig() {
        if(_configCached != null)
            return _configCached;

        try {
            CommentedConfigurationNode node = configLoader.load();

            _configCached = node.get(GreenCutsConfig.class);
        }
        catch (ConfigurateException ex) {
            LOGGER.error(ServerAPI.Logger.Error("[ERROR]Failed to load greencuts config."));
        }

        return _configCached;
    }

    public static void saveConfig() {
        CommentedConfigurationNode node = CommentedConfigurationNode.root();
        try {
            node.set(GreenCutsConfig.class, _configCached);
            configLoader.save(node);
        }
        catch (ConfigurateException ex) {
            LOGGER.error(ServerAPI.Logger.Error("[ERROR]Failed to save greencuts config."));
        }
    }

    private static final HoconConfigurationLoader configLoader = HoconConfigurationLoader.builder()
            .path(Path.of(ServerAPI.CONFIG_PATH + "greencuts.conf"))
            .build();
    private static GreenCutsConfig _configCached = null;
}
