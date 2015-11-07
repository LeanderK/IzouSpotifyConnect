package leanderk.izou.spotifyconnect;

import com.shuffle.scplayer.core.PlayerListener;
import com.shuffle.scplayer.core.SpotifyConnectPlayer;
import com.shuffle.scplayer.core.Track;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.frameworks.music.player.template.PlayerController;

/**
 * @author LeanderK
 * @version 1.0
 */
public class ConnectPlayerController extends PlayerController {
    public static final String ID = ConnectPlayerController.class.getCanonicalName();

    public ConnectPlayerController(Context context, SpotifyConnectPlayer spotifyConnectPlayer) {
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
                startPlaying();
            }

            @Override
            public void onInactive() {}

            @Override
            public void onTokenLost() {}

            @Override
            public void onVolumeChanged(short i) {}

            @Override
            public void onLoggedIn() {}

            @Override
            public void onLoggedOut() {}
        });
    }

    /**
     * This method will be called in a loop.
     */
    @Override
    public void activatorStarts() {
        stop();
    }
}
