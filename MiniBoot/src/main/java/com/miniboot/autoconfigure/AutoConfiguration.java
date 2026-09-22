package com.miniboot.autoconfigure;

/**
 * Marker for MiniBoot auto-configuration classes listed in {@code META-INF/miniboot.factories}.
 */
public interface AutoConfiguration {

    /**
     * Contribute beans / runtime wiring into the boot context.
     */
    void configure(AutoConfigurationContext context);
}
