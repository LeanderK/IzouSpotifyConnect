package leanderk.izou.spotifyconnect;

import com.shuffle.scplayer.core.PlayerListener;
import com.shuffle.scplayer.core.SpotifyConnectPlayer;
import com.shuffle.scplayer.core.Track;
import org.intellimate.izou.events.EventLifeCycle;
import org.intellimate.izou.identification.Identification;
import org.intellimate.izou.identification.IdentificationManager;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.frameworks.music.events.StartMusicRequest;
import org.intellimate.izou.sdk.frameworks.music.player.TrackInfo;
import org.intellimate.izou.sdk.frameworks.music.player.template.Player;
import org.intellimate.izou.sdk.frameworks.music.player.template.PlayerController;
import org.intellimate.izou.sdk.frameworks.presence.consumer.PresenceEventUser;
import org.intellimate.izou.sdk.frameworks.presence.consumer.PresenceResourceUser;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author LeanderK
 * @version 1.0
 */
public class ConnectPlayerController extends PlayerController implements PresenceEventUser, PresenceResourceUser {
    public static final String ID = ConnectPlayerController.class.getCanonicalName();
    private Player player;


    public ConnectPlayerController(Context context, SpotifyConnectPlayer spotifyConnectPlayer, Consumer<SpotifyConnectPlayer> reset, Consumer<SpotifyConnectPlayer> login, Consumer<SpotifyConnectPlayer> logout) {
        super(context, ID);
        spotifyConnectPlayer.addPlayerListener(new PlayerListener() {
            @Override
            public void onPlay() {}

            @Override
            public void onPause() {}

            @Override
            public void onSeek(int i) {}

            @Override
            public void onTrackChanged(Track track) {}

            @Override
            public void onNextTrack(Track track) {}

            @Override
            public void onPreviousTrack(Track track) {}

            @Override
            public void onShuffle(boolean b) {}

            @Override
            public void onRepeat(boolean b) {}

            @Override
            public void onActive() {
                Optional<Identification> ownIdentification = IdentificationManager.getInstance()
                        .getIdentification(ConnectPlayerController.this);
                Optional<Identification> playerIdentification = IdentificationManager.getInstance()
                        .getIdentification(player);
                if (!ownIdentification.isPresent() || !playerIdentification.isPresent()) {
                    error("unable to obtain identification");
                    return;
                }
                StartMusicRequest.createStartMusicRequest(ownIdentification.get(), playerIdentification.get(), (TrackInfo) null, true)
                        .map(event -> event.addEventLifeCycleListener(EventLifeCycle.CANCELED, cycle -> reset.accept(spotifyConnectPlayer)))
                        .ifPresent(event -> fire(event, 5));
            }

            @Override
            public void onInactive() {}

            @Override
            public void onTokenLost() {}

            @Override
            public void onVolumeChanged(short i) {}
        });

        registerPresenceCallback(ev -> login.accept(spotifyConnectPlayer), false, true);

        registerLeavingCallback(ev -> logout.accept(spotifyConnectPlayer), false);

        if (isPresent())
            login.accept(spotifyConnectPlayer);
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
        super.setPlayer(player);
    }

    /**
     * This method will be called in a loop.
     */
    @Override
    public void activatorStarts() {
        stop();
    }
}
