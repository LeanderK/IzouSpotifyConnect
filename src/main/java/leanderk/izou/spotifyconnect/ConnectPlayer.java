package leanderk.izou.spotifyconnect;

import com.shuffle.scplayer.core.AudioPlayer;
import com.shuffle.scplayer.core.PlayerListener;
import com.shuffle.scplayer.core.SpotifyConnectPlayer;
import com.shuffle.scplayer.core.Track;
import org.intellimate.izou.events.EventModel;
import org.intellimate.izou.identification.Identifiable;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.frameworks.music.player.*;
import org.intellimate.izou.sdk.frameworks.music.player.template.Player;
import org.intellimate.izou.sdk.frameworks.music.resources.CommandResource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

/**
 * @author LeanderK
 * @version 1.0
 */
public class ConnectPlayer extends Player<Void> implements PlayerListener {
    public static final String ID = ConnectPlayer.class.getCanonicalName();
    private final SpotifyConnectPlayer player;
    private final AudioPlayer audioListener;
    private final Consumer<SpotifyConnectPlayer> reset;
    private final AudioListenerGuard audioListenerGuard;

    /**
     * creates a new output-plugin with a new id
     *
     * @param context            context
     * @param activator          the activator which is able to start the Player if the Player is not able to start from
     *                           request from other addons
     */
    public ConnectPlayer(Context context, Identifiable activator, SpotifyConnectPlayer player, Consumer<SpotifyConnectPlayer> reset) {
        super(context, ID, false, activator, true, true, true, false, true);
        this.player = player;
        player.addPlayerListener(this);
        audioListener = ((AudioPlayer) player.getAudioListener());
        audioListenerGuard = new AudioListenerGuard(context, audioListener);
        player.setAudioListener(audioListenerGuard);
        this.reset = reset;
        getCommandHandler().setJumpProgressController(progress -> {
            if (getCurrentProgress().getLength() <= progress.getPosition() && progress.getPosition() >= 0) {
                player.seek((int) progress.getPosition());
            }
        });
        getCommandHandler().setNextPreviousController(command -> {
            switch (command) {
                case CommandResource.NEXT: player.next();
                    break;
                case CommandResource.PREVIOUS: player.prev();
                    break;
            }
        });
        getCommandHandler().setPlayPauseController(command -> {
            switch (command) {
                case CommandResource.PLAY: player.play();
                    break;
                case CommandResource.PAUSE: player.pause();
                    break;
            }
        });
        getCommandHandler().setVolumeChangeableController(volume -> {
            double i = volume.getVolume() / 100 / 655.35;
            player.volume((short) i);
        });
    }


    /**
     * this method call must mute the plugin.
     */
    @Override
    public void mute() {
        audioListener.mute();
    }

    /**
     * this method call must un-mute the plugin.
     */
    @Override
    public void unMute() {
        audioListener.mute();
    }

    /**
     * this method call must stop the sound.<br>
     * NEVER CALL THIS METHOD DIRECTLY, USE {@link #stopMusicPlayback()}.
     */
    @Override
    public void stopSound() {
        audioListenerGuard.deactivate();
    }

    /**
     * this method will be called if a request was cached which was eligible to start the music.<br>
     * please check the events resources for parameters (if expected).
     *
     * @param eventModel the cause
     */
    @Override
    public void play(EventModel eventModel) {
        audioListenerGuard.activate();
    }

    @Override
    public void onPlay() {
        if(!isRunning())
            return;
        if (getPlaybackState() != PlaybackState.PLAY)
            resumePlaying();
    }

    @Override
    public void onPause() {
        if(!isRunning())
            return;
        if (getPlaybackState() != PlaybackState.PAUSE)
            pausePlaying();
    }

    @Override
    public void onSeek(int i) {
        if(!isRunning())
            return;
        if (player.getPlayingTrack() != null && player.getPlayingTrack().getDuration() != -1)
            updatePlayInfo(new Progress(player.getPlayingTrack().getDuration(), i));
    }

    @Override
    public void onTrackChanged(Track track) {
        if(!isRunning())
            return;
        trackUpdate(track);
    }

    private void trackUpdate(Track track) {
        TrackInfo newTrack = getTrackInfofromTrack(track);

        if (getCurrentPlaylist() != null && getCurrentPlaylist().getCurrent() != null &&
                !getCurrentPlaylist().getCurrent().equals(newTrack)) {
            updatePlayInfo(newTrack);
            updatePlayInfo(new Progress(track.getDuration(), 0));
        }
    }

    @Override
    public void onNextTrack(Track track) {
        if(!isRunning())
            return;
        trackUpdate(track);
    }

    @Override
    public void onPreviousTrack(Track track) {
        if(!isRunning())
            return;
        trackUpdate(track);
    }

    @Override
    public void onShuffle(boolean b) {
        if(!isRunning())
            return;
        Playlist current = getCurrentPlaylist();
        if (current == null)
            return;
        HashSet<PlaybackMode> modes = new HashSet<>(current.getPlaybackModes());
        if (b) {
            modes.add(PlaybackMode.SHUFFLE);
        } else {
            modes.remove(PlaybackMode.SHUFFLE);
        }
        updatePlayInfo(new Playlist(current.getQueue(), current.getName().orElse(null), new ArrayList<>(modes),
                current.getPosition(), current.getData().orElse(null)));
    }

    @Override
    public void onRepeat(boolean b) {
        if(!isRunning())
            return;
        Playlist current = getCurrentPlaylist();
        if (current == null)
            return;
        HashSet<PlaybackMode> modes = new HashSet<>(current.getPlaybackModes());
        if (b) {
            modes.add(PlaybackMode.REPEAT);
        } else {
            modes.remove(PlaybackMode.REPEAT);
        }
        updatePlayInfo(new Playlist(current.getQueue(), current.getName().orElse(null), new ArrayList<>(modes),
                current.getPosition(), current.getData().orElse(null)));
    }

    @Override
    public void onActive() {}

    @Override
    public void onInactive() {
        if(!isRunning())
            return;
        audioListenerGuard.deactivate();
        stopMusicPlayback();
    }

    @Override
    public void onTokenLost() {
        if(!isRunning())
            return;
        stopMusicPlayback();
    }

    @Override
    public void onVolumeChanged(short volume) {
        if(!isRunning())
            return;
        float volumePercent = (float) (volume / 655.35);
        Volume.createVolume((int) (volumePercent * 100)).ifPresent(this::updatePlayInfo);
    }

    @Override
    public void onLoggedIn() {}

    @Override
    public void onLoggedOut() {}

    private TrackInfo getTrackInfofromTrack(Track track) {
        return new TrackInfo(track.getName(), track.getArtist(), track.getAlbum(), null, null, track.getUri());
    }
}
