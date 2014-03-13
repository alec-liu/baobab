package net.milanaleksic.baobab.util;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.milanaleksic.baobab.TransformerException;
import net.milanaleksic.baobab.integration.loader.Loader;

import java.util.Optional;

/**
 * User: Milan Aleksic
 * Date: 4/19/13
 * Time: 9:46 AM
 */
public class Configuration {

    private static final Config reference = ConfigFactory.load();

    public static ImmutableMap<String, Class<?>> loadStringToClassMapping(String configName) {
        ImmutableMap.Builder<String, Class<?>> builder = ImmutableMap.builder();
        final Config configuration = reference.getConfig(configName);
        configuration.root().unwrapped().entrySet().forEach(entry -> {
            try {
                builder.put(entry.getKey(), Class.forName(entry.getValue().toString()));
            } catch (ClassNotFoundException e) {
                throw new TransformerException("Configuration could not be loaded for entry: " + entry.getKey(), e);
            }
        });
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    public static <T> ImmutableMap<Class<?>, T> loadClassToInstanceMapping(String configName, Optional<Loader> loader) {
        ImmutableMap.Builder<Class<?>, T> builder = ImmutableMap.builder();
        final Config configuration = reference.getConfig(configName);
        if (configuration.isEmpty())
            return ImmutableMap.of();
        configuration.root().unwrapped().entrySet().forEach(entry -> {
            try {
                final Class<?> classWhichIsMaybeWrapper = Class.forName(entry.getKey());
                final Class<?> clazz = Class.forName(entry.getValue().toString());

                // To be both Guice- and Spring- able, class must be initialized via no-arg constructor
                // thus, it is not allowed to use constructor injection, only property injection
                if (clazz.getConstructor() == null)
                    throw new TransformerException("Transformer supports only extension classes with default constructor");
                T raw = (T) ObjectUtil.createInstanceForType(clazz);
                if (loader.isPresent())
                    loader.get().load(raw);
                try {
                    Class primitiveClass = (Class) classWhichIsMaybeWrapper.getField("TYPE").get(null);
                    builder.put(primitiveClass, raw);
                } catch (NoSuchFieldException ignored) {
                    builder.put(classWhichIsMaybeWrapper, raw);
                }
            } catch (Exception e) {
                throw new TransformerException("Configuration could not be loaded for entry: " + entry.getKey(), e);
            }
        });
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    public static <T> ImmutableMap<String, T> loadStringToInstanceMapping(String configName, Optional<Loader> loader) {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        final Config configuration = reference.getConfig(configName);
        if (configuration.isEmpty())
            return ImmutableMap.of();
        configuration.root().unwrapped().entrySet().forEach(entry -> {
            try {
                T raw = (T) ObjectUtil.createInstanceForType(Class.forName(entry.getValue().toString()));
                if (loader.isPresent())
                    loader.get().load(raw);
                builder.put(entry.getKey(), raw);
            } catch (Exception e) {
                throw new TransformerException("Configuration could not be loaded for entry: " + entry.getKey(), e);
            }
        });
        return builder.build();
    }
}
