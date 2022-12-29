package squeek.applecore.mixinplugin;

import static squeek.applecore.mixinplugin.TargetedMod.*;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public enum Mixins {

    //
    // IMPORTANT: Do not make any references to any mod from this file. This file is loaded quite early on and if
    // you refer to other mods you load them as well. The consequence is: You can't inject any previously loaded
    // classes!
    // Exception: Tags.java, as long as it is used for Strings only!
    //
    BlockCactusMixin(new Builder()
            .addMixinClasses("minecraft.BlockCactusMixin")
            .addTargetedMod(VANILLA)
            .setPhase(Phase.EARLY)),
    BlockCakeMixin(new Builder()
            .addMixinClasses("minecraft.BlockCakeMixin")
            .addTargetedMod(VANILLA)
            .setPhase(Phase.EARLY)),
    BlockCocoaMixin(new Builder()
            .addMixinClasses("minecraft.BlockCocoaMixin")
            .addTargetedMod(VANILLA)
            .setPhase(Phase.EARLY)),
    BlockCropsMixin(new Builder()
            .addMixinClasses("minecraft.BlockCropsMixin")
            .addTargetedMod(VANILLA)
            .setPhase(Phase.EARLY)),
    BlockMushroomMixin(new Builder()
            .addMixinClasses("minecraft.BlockMushroomMixin")
            .addTargetedMod(VANILLA)
            .setPhase(Phase.EARLY)),
    BlockNetherWartMixin(new Builder()
            .addMixinClasses("minecraft.BlockNetherWartMixin")
            .addTargetedMod(VANILLA)
            .setPhase(Phase.EARLY)),
    BlockReedMixin(new Builder()
            .addMixinClasses("minecraft.BlockReedMixin")
            .addTargetedMod(VANILLA)
            .setPhase(Phase.EARLY)),
    BlockSaplingMixin(new Builder()
            .addMixinClasses("minecraft.BlockSaplingMixin")
            .addTargetedMod(VANILLA)
            .setPhase(Phase.EARLY)),
    BlockStemMixin(new Builder()
            .addMixinClasses("minecraft.BlockStemMixin")
            .addTargetedMod(VANILLA)
            .setPhase(Phase.EARLY)),
    EntityPlayerMixin(new Builder()
            .addMixinClasses("minecraft.EntityPlayerMixin")
            .addTargetedMod(VANILLA)
            .setPhase(Phase.EARLY)),
    FoodStatsMixin(new Builder()
            .addMixinClasses("minecraft.FoodStatsMixin")
            .addTargetedMod(VANILLA)
            .setPhase(Phase.EARLY)),
    GuiScreenMixin(new Builder()
            .addMixinClasses("minecraft.GuiScreenMixin")
            .setSide(Side.CLIENT)
            .addTargetedMod(VANILLA)
            .setPhase(Phase.EARLY)),
    ItemRendererMixin(new Builder()
            .addMixinClasses("minecraft.ItemRendererMixin")
            .setSide(Side.CLIENT)
            .addTargetedMod(VANILLA)
            .setPhase(Phase.EARLY)),

    GuiDrawMixin(new Builder()
            .addMixinClasses("codechickenlib.GuiDrawMixin")
            .setSide(Side.CLIENT)
            .addTargetedMod(CODECHICKEN_LIB)
            .setPhase(Phase.EARLY)),

    BlockPamFruitMixin(
            new Builder().addMixinClasses("harvestcraft.BlockPamFruitMixin").addTargetedMod(HARVESTCRAFT)),
    BlockPamSaplingMixin(
            new Builder().addMixinClasses("harvestcraft.BlockPamSaplingMixin").addTargetedMod(HARVESTCRAFT)),

    BerryBushMixin(new Builder().addMixinClasses("natura.BerryBushMixin").addTargetedMod(NATURA)),
    NetherBerryBushMixin(
            new Builder().addMixinClasses("natura.NetherBerryBushMixin").addTargetedMod(NATURA));

    public final List<String> mixinClasses;
    public final Phase phase;
    private final Side side;
    public final List<TargetedMod> targetedMods;
    public final List<TargetedMod> excludedMods;

    private static class Builder {
        private final List<String> mixinClasses = new ArrayList<>();
        private Side side = Side.BOTH;
        private Phase phase = Phase.LATE;
        private final List<TargetedMod> targetedMods = new ArrayList<>();
        private final List<TargetedMod> excludedMods = new ArrayList<>();

        public Builder() {}

        public Builder addMixinClasses(String... mixinClasses) {
            this.mixinClasses.addAll(Arrays.asList(mixinClasses));
            return this;
        }

        public Builder setPhase(Phase phase) {
            this.phase = phase;
            return this;
        }

        public Builder setSide(Side side) {
            this.side = side;
            return this;
        }

        public Builder addTargetedMod(TargetedMod mod) {
            this.targetedMods.add(mod);
            return this;
        }

        public Builder addExcludedMod(TargetedMod mod) {
            this.excludedMods.add(mod);
            return this;
        }
    }

    Mixins(Builder builder) {
        this.mixinClasses = builder.mixinClasses;
        this.side = builder.side;
        this.targetedMods = builder.targetedMods;
        this.excludedMods = builder.excludedMods;
        this.phase = builder.phase;
        if (this.targetedMods.isEmpty()) {
            throw new RuntimeException("No targeted mods specified!");
        }
    }

    private boolean shouldLoadSide() {
        return (side == Side.BOTH
                || (side == Side.SERVER && FMLLaunchHandler.side().isServer())
                || (side == Side.CLIENT && FMLLaunchHandler.side().isClient()));
    }

    private boolean allModsLoaded(List<TargetedMod> targetedMods, Set<String> loadedCoreMods, Set<String> loadedMods) {
        if (targetedMods.isEmpty()) return false;

        for (TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) continue;

            // Check coremod first
            if (!loadedCoreMods.isEmpty()
                    && target.coreModClass != null
                    && !loadedCoreMods.contains(target.coreModClass)) return false;
            else if (!loadedMods.isEmpty() && target.modId != null && !loadedMods.contains(target.modId)) return false;
        }

        return true;
    }

    private boolean noModsLoaded(List<TargetedMod> targetedMods, Set<String> loadedCoreMods, Set<String> loadedMods) {
        if (targetedMods.isEmpty()) return true;

        for (TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) continue;

            // Check coremod first
            if (!loadedCoreMods.isEmpty()
                    && target.coreModClass != null
                    && loadedCoreMods.contains(target.coreModClass)) return false;
            else if (!loadedMods.isEmpty() && target.modId != null && loadedMods.contains(target.modId)) return false;
        }

        return true;
    }

    public boolean shouldLoad(Set<String> loadedCoreMods, Set<String> loadedMods) {
        return (shouldLoadSide()
                && allModsLoaded(targetedMods, loadedCoreMods, loadedMods)
                && noModsLoaded(excludedMods, loadedCoreMods, loadedMods));
    }

    enum Side {
        BOTH,
        CLIENT,
        SERVER
    }

    public enum Phase {
        EARLY,
        LATE,
    }
}
