package uk.ac.bsfc.sbp.utils.config;

import java.util.ArrayList;
import java.util.List;

@ConfigFile("features")
public class FeatureConfig implements ReloadableConfig {

    @Comment("Settings for the mob stacker feature")
    public MobStacker mobStacker = new MobStacker();
    public SpawnerStacker spawnerStacker = new SpawnerStacker();

    public static class MobStacker {

        @Comment("Enable or disable the mob stacker")
        public boolean enabled = false;

        @Comment("If true, disables AI for stacked mobs")
        public boolean disableAI = false;

        @Comment("Maximum number of mobs in a single stack")
        public int maxStack = 50;

        @Comment("Maximum number of stacked mobs allowed per chunk")
        public int maxStackPerChunk = 8;
    }

    public static class SpawnerStacker {
        @Comment("Basic settings: enabling, stack limits, and merge rules")
        public boolean enabled = true;
        public int maxStack = 64;
        public int maxSpawnerStacksPerChunk = 4;
        public boolean autoMerge = true;
        public boolean mustMatchSpawnerType = true;

        @Comment("Visual settings: hologram display above stacked spawners")
        public boolean showHolograms = true;

        @Comment("Hologram text format (placeholders: {type}, {stack}, {level})")
        public List<String> hologramLines = new ArrayList<>(List.of(
                "&e{type} Spawner",
                "&7Stack: &b{stack}",
                "&7Level: &a{level}"
        ));

        @Comment("Spawn count multiplier per additional stack (0.10 = spawn count increases by 0.10 per stack)")
        public double spawnMultiplierPerStack = 1.0;

        @Comment("Spawn count multiplier per level (0.25 = spawn count increases by 0.25 per level)")
        public double spawnMultiplierPerLevel = 0.0;

        @Comment("Spawn delay reduction per level in ticks (10 = 0.5 seconds faster per level)")
        public int spawnDelayReductionPerLevel = 10;

        @Comment("Upgrade system: level cap and cost behavior")
        public int maxLevel = 5;

        @Comment("Upgrade cost = baseUpgradeCost * (level + 1)")
        public int baseUpgradeCost = 1000;

        @Comment("Drop behavior: determines whether stacked spawners drop as a single item")
        public boolean dropStackedSpawner = true;
    }
}