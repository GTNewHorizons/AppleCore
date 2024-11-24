package squeek.applecore.helpers;

import codechicken.core.launch.CodeChickenCorePlugin;
import cpw.mods.fml.common.versioning.ComparableVersion;

public class CCLLegacyHelper {

    public static boolean useLegacyMixin() {
        return !useNewMixin();
    }

    @SuppressWarnings("deprecation")
    public static boolean useNewMixin() {
        String version;
        try {
            version = CodeChickenCorePlugin.version;
        } catch (Exception e) {
            return false;
        }
        System.out.println(
                "1.3.10 compareTo " + version
                        + " = "
                        + new ComparableVersion("1.3.10").compareTo(new ComparableVersion(version)));
        return new ComparableVersion("1.3.10").compareTo(new ComparableVersion(version)) <= 0;
    }

}
