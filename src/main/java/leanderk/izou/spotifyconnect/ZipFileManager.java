package leanderk.izou.spotifyconnect;

import org.intellimate.izou.sdk.ZipFileManagerImpl;
import ro.fortsoft.pf4j.PluginWrapper;

/**
 * @author LeanderK
 * @version 1.0
 */
public class ZipFileManager extends ZipFileManagerImpl {
    /**
     * Constructor to be used by plugin manager for plugin instantiation.
     * Your plugins have to provide constructor with this exact signature to
     * be successfully loaded by manager.
     *
     * @param wrapper the PluginWrapper to assign the ZipFileManager to
     */
    public ZipFileManager(PluginWrapper wrapper) {
        super(wrapper);
    }
}
