package squeek.applecore.mixinplugin;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import squeek.applecore.AppleCore;

@LateMixin
public class AppleCoreLateMixins implements ILateMixinLoader {
    public String getMixinConfig() {
        return "mixins.AppleCore.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        final List<String> mixins = new ArrayList<>();
        final List<String> notLoading = new ArrayList<>();
        for (Mixins mixin : Mixins.values()) {
            if (mixin.phase == Mixins.Phase.LATE) {
                if (mixin.shouldLoad(Collections.emptySet(), loadedMods)) {
                    mixins.addAll(mixin.mixinClasses);
                } else {
                    notLoading.addAll(mixin.mixinClasses);
                }
            }
        }
        AppleCore.Log.info("Not loading the following LATE mixins: {}", notLoading.toString());
        return mixins;
    }
}
