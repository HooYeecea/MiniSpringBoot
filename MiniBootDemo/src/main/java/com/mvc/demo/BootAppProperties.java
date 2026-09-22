package com.mvc.demo;

import com.miniboot.annotation.ConfigurationProperties;
import com.miniioccontainer.annotation.MyComponent;

/**
 * Demo {@code @ConfigurationProperties} bean (scanned via {@code com.mvc.demo}).
 * Bound from {@code app.*} keys after the IoC context refreshes.
 */
@MyComponent
@ConfigurationProperties(prefix = "app")
public class BootAppProperties {

    private String name = "unset";
    private boolean debug = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public String toString() {
        return "BootAppProperties{name='" + name + "', debug=" + debug + '}';
    }
}
