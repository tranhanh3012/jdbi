/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.config;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A registry of {@link JdbiConfig} instances by type.
 *
 * @see Configurable
 */
public class ConfigRegistry {
    private final Object createLock = new Object();
    private final Map<Class<? extends JdbiConfig<?>>, JdbiConfig<?>> configs = new ConcurrentHashMap<>();

    /**
     * Creates a new config registry.
     */
    public ConfigRegistry() {
        configs.put(JdbiCaches.class, new JdbiCaches());
    }

    private ConfigRegistry(ConfigRegistry that) {
        that.configs.forEach((type, config) -> {
            JdbiConfig<?> copy = config.createCopy();
            copy.setRegistry(this);
            configs.put(type, copy);
        });
    }

    /**
     * Returns this registry's instance of the given config class. Creates an instance on-demand if this registry does
     * not have one of the given type yet.
     *
     * @param configClass the config class type.
     * @param <C>         the config class type.
     * @return the given config class instance that belongs to this registry.
     */
    public <C extends JdbiConfig<C>> C get(Class<C> configClass) {
        // we would computeIfAbsent if not for JDK-8062841 >:(
        final JdbiConfig<?> lookup = configs.get(configClass);
        if (lookup != null) {
            return configClass.cast(lookup);
        }
        synchronized (createLock) {
            try {
                C config = configClass.getDeclaredConstructor().newInstance();
                config.setRegistry(this);
                return Optional.ofNullable(configClass.cast(configs.putIfAbsent(configClass, config))).orElse(config);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Unable to instantiate config class " + configClass
                    + ". Is there a public no-arg constructor?", e);
            }
        }
    }

    /**
     * Returns a copy of this config registry.
     *
     * @return a copy of this config registry
     * @see JdbiConfig#createCopy() config objects in the returned registry are copies of the corresponding
     * config objects from this registry.
     */
    public ConfigRegistry createCopy() {
        return new ConfigRegistry(this);
    }

    /**
     * Mark a Jdbi object which should have a {@code ConfigRegistry} injected before use.
     */
    public interface Injected {
        /**
         * The registry will inject itself into the configuration object.
         * This can be useful if you need to look up dependencies.
         * You will get a new registry after being copied.
         * @param registry the registry that owns this configuration object
         */
        default void setRegistry(ConfigRegistry registry) {}
    }
}
