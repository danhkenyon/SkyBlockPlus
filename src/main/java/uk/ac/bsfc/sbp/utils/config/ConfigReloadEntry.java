package uk.ac.bsfc.sbp.utils.config;

import java.lang.reflect.Method;

public record ConfigReloadEntry(
        String name,
        String fileName,
        ReloadableConfig instance,
        Method reloadMethod
) {
}
