package com.bytelaw;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ColorableBooksConfig {
    public static final ForgeConfigSpec CLIENT;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        CLIENT = Client.setupValues(builder);
    }

    public static final class Client {
        public static ForgeConfigSpec.BooleanValue SHOW_COLOR_LIST;

        private static ForgeConfigSpec setupValues(ForgeConfigSpec.Builder builder) {
            SHOW_COLOR_LIST = builder.comment("True if show color code list in the edit screen.").define("showColorList", true);
            return builder.build();
        }
    }
}
