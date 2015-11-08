package leanderk.izou.spotifyconnect;

import com.shuffle.scplayer.core.AudioListener;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LeanderK
 * @version 1.0
 */
public class AudioListenerGuard implements AudioListener {
    private boolean activate = false;
    private final AudioListener audioListener;

    private boolean play = false;
    private Lock playLock = new ReentrantLock();
    private short volume = -1;
    private Lock volumeLock = new ReentrantLock();

    public AudioListenerGuard(AudioListener audioListener) {
        this.audioListener = audioListener;
    }

    public void activate() {
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
        activate = true;
    }

    public void deactivate() {
        activate = false;
        audioListener.onInactive();
    }

    @Override
    public void onActive() {}

    @Override
    public void onInactive() {
        if (activate)
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
        if (activate)
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
        if (activate)
            audioListener.onPause();
    }

    @Override
    public void onTokenLost() {
        if(activate)
            audioListener.onTokenLost();
    }

    @Override
    public void onAudioFlush() {
        if (activate)
            audioListener.onAudioFlush();
    }

    @Override
    public void onAudioData(byte[] bytes) {
        if (activate)
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
        if (activate)
            audioListener.onVolumeChanged(i);
    }

    @Override
    public void close() {
        audioListener.close();
    }
}
