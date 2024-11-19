package squeek.applecore.mixinplugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import codechicken.core.launch.CodeChickenCorePlugin;

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
        return compareVersion("1.3.10", version);
    }

    /**
     * @return {@code true} if {@code v2} is equal to or newer than {@code v1}.
     */
    private static boolean compareVersion(String v1, String v2) {
        String[] parts1 = StringUtils.splitPreserveAllTokens(v1, '.');
        String[] parts2 = StringUtils.splitPreserveAllTokens(v2, '.');
        int commonLength = Math.min(parts1.length, parts2.length);

        for (int i = 0; i < commonLength; i++) {
            int comparison = compare(parts1[i], parts2[i]);
            if (comparison != 0) {
                return comparison < 0;
            }
        }

        return parts1.length <= parts2.length;
    }

    private static int compare(String s1, String s2) {
        Pair<Integer, String> p1 = extractNumericPrefix(s1);
        Pair<Integer, String> p2 = extractNumericPrefix(s2);

        int intComparison = p1.getLeft().compareTo(p2.getLeft());

        if (intComparison == 0) {
            // the numeric prefixes are equal
            return p1.getRight().compareTo(p2.getRight());
        }
        return intComparison;
    }

    private static Pair<Integer, String> extractNumericPrefix(String s) {
        int i;
        for (i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') {
                break;
            }
        }
        if (i == 0) {
            // no numeric prefix
            return Pair.of(0, s);
        }
        return Pair.of(Integer.parseInt(s.substring(0, i)), s.substring(i));
    }

}
