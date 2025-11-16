package uk.ac.bsfc.sbp.utils.config;

@ConfigFile("features")
public class FeatureConfig implements ReloadableConfig {

    @Comment("Settings for the mob stacker feature")
    public MobStacker mobStacker = new MobStacker();

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
}
