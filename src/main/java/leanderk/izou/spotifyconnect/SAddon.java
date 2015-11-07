package leanderk.izou.spotifyconnect;

import com.shuffle.scplayer.core.SpotifyConnectPlayer;
import com.shuffle.scplayer.core.SpotifyConnectPlayerImpl;
import org.intellimate.izou.activator.ActivatorModel;
import org.intellimate.izou.events.EventsControllerModel;
import org.intellimate.izou.output.OutputExtensionModel;
import org.intellimate.izou.output.OutputPluginModel;
import org.intellimate.izou.sdk.addon.AddOn;
import org.intellimate.izou.sdk.contentgenerator.ContentGenerator;

import java.io.File;
import java.util.function.Consumer;

/**
 * @author LeanderK
 * @version 1.0
 */
public class SAddon extends AddOn {
    public static final String ID = SAddon.class.getCanonicalName();

    /**
     * The default constructor for AddOns
     */
    public SAddon() {
        super(ID);
    }

    /**
     * This method gets called before registering
     */
    @Override
    public void prepare() {
        File pathToAppkey = new File(getContext().getFiles().getResourceLocation(),
                getContext().getPropertiesAssistant().getProperty("pathToAppkey"));
        File pathToLib = new File(getContext().getFiles().getResourceLocation(),
                getContext().getPropertiesAssistant().getProperty("pathToLibSpotify"));

        SpotifyConnectPlayer player = new SpotifyConnectPlayerImpl(pathToAppkey, pathToLib.getAbsolutePath());
        player.login(getContext().getPropertiesAssistant().getProperty("spotify-username"),
                getContext().getPropertiesAssistant().getProperty("spotify-password"));
        player.setPlayerName(getContext().getPropertiesAssistant().getProperty("name"));

        Consumer<SpotifyConnectPlayer> reset = player1 -> {
            player1.logout();
            player1.getAudioListener().onInactive();
            player1.login(getContext().getPropertiesAssistant().getProperty("spotify-username"),
                    getContext().getPropertiesAssistant().getProperty("spotify-password"));
        };

        ConnectPlayerController controller = new ConnectPlayerController(getContext(), player);
        ConnectPlayer connectPlayer = new ConnectPlayer(getContext(), controller, player, reset);
        controller.setPlayer(connectPlayer);
        getContext().getActivators().addActivator(controller);
        getContext().getOutput().addOutputPlugin(connectPlayer);
    }

    /**
     * Use this method to register (if needed) your Activators.
     *
     * @return Array containing Instances of Activators
     */
    @Override
    public ActivatorModel[] registerActivator() {
        return new ActivatorModel[0];
    }

    /**
     * Use this method to register (if needed) your ContentGenerators.
     *
     * @return Array containing Instances of ContentGenerators
     */
    @Override
    public ContentGenerator[] registerContentGenerator() {
        return new ContentGenerator[0];
    }

    /**
     * Use this method to register (if needed) your EventControllers.
     *
     * @return Array containing Instances of EventControllers
     */
    @Override
    public EventsControllerModel[] registerEventController() {
        return new EventsControllerModel[0];
    }

    /**
     * Use this method to register (if needed) your OutputPlugins.
     *
     * @return Array containing Instances of OutputPlugins
     */
    @Override
    public OutputPluginModel[] registerOutputPlugin() {
        return new OutputPluginModel[0];
    }

    /**
     * Use this method to register (if needed) your Output.
     *
     * @return Array containing Instances of OutputExtensions
     */
    @Override
    public OutputExtensionModel[] registerOutputExtension() {
        return new OutputExtensionModel[0];
    }
}
