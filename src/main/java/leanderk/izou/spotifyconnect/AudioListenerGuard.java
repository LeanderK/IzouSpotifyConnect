package leanderk.izou.spotifyconnect;

import com.shuffle.scplayer.core.AudioListener;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.util.AddOnModule;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LeanderK
 * @version 1.0
 */
public class AudioListenerGuard extends AddOnModule implements AudioListener {
    public static final String ID = AudioListenerGuard.class.getCanonicalName();

    private boolean activ = false;
    private final AudioListener audioListener;

    private boolean play = true;
    private Lock playLock = new ReentrantLock();
    private short volume = -1;
    private Lock volumeLock = new ReentrantLock();

    public AudioListenerGuard(Context context, AudioListener audioListener) {
        super(context, ID);
        this.audioListener = audioListener;
    }

    public void activate() {
        if (activ)
            return;
        audioListener.onActive();
        try {
            playLock.lock();
            if (play) {
                audioListener.onPlay();
            } else {
                audioListener.onPause();
            }
        } finally {
            playLock.unlock();
        }
        try {
            volumeLock.lock();
            if (volume != -1)
                audioListener.onVolumeChanged(volume);
        } finally {
            volumeLock.unlock();
        }
        activ = true;
    }

    public void deactivate() {
        if (!activ)
            return;
        activ = false;
        audioListener.onInactive();
    }

    @Override
    public void onActive() {}

    @Override
    public void onInactive() {
        if (activ)
            audioListener.onInactive();
    }

    @Override
    public void onPlay() {
        try {
            playLock.lock();
            play = true;
        } finally {
            playLock.unlock();
        }
        if (activ)
            audioListener.onPlay();
    }

    @Override
    public void onPause() {
        try {
            playLock.lock();
            play = false;
        } finally {
            playLock.unlock();
        }
        if (activ)
            audioListener.onPause();
    }

    @Override
    public void onTokenLost() {
        if(activ)
            audioListener.onTokenLost();
    }

    @Override
    public void onAudioFlush() {
        if (activ)
            audioListener.onAudioFlush();
    }

    @Override
    public void onAudioData(byte[] bytes) {
        if (activ)
            audioListener.onAudioData(bytes);
    }

    @Override
    public void onVolumeChanged(short i) {
        try {
            volumeLock.lock();
            volume = i;
        } finally {
            volumeLock.unlock();
        }
        if (activ)
            audioListener.onVolumeChanged(i);
    }

    @Override
    public void close() {
        audioListener.close();
    }
}
