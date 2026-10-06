package net.silentchaos512.scalinghealth.config;

import com.google.common.collect.ImmutableList;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.silentchaos512.lib.util.Color;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A list of colors loaded from a config file. Uses Silent Lib's Color class.
 */
public class ColorList {
    private LazyValue<List<Integer>> list;
    private final ModConfigSpec.ConfigValue<List<? extends String>> config;

    public ColorList(ModConfigSpec.Builder builder, String path, String comment, int... defaults) {
        // Default list of formatted values
        List<String> defaultList = Arrays
                .stream(defaults)
                .mapToObj(Color::format)
                .collect(Collectors.toList());

        // Load a string list
        this.config = builder
                .comment(comment)
                .defineList(path, defaultList, o -> o instanceof String && Color.validate((String) o));

        recalculate();
    }

    public void recalculate()  {
        list = new LazyValue<>(() -> ImmutableList.copyOf(
                config.get()
                        .stream()
                        .map(Color::parseInt)
                        .collect(Collectors.toList())));
    }

    public List<Integer> get() {
        return list.get();
    }

    /**
     * Small non-synchronized lazy value matching the removed Silent Lib
     * helper: null is cached, while a throwing supplier is retried.
     */
    private static final class LazyValue<T> {
        private java.util.function.Supplier<? extends T> supplier;
        private T value;
        private boolean initialized;

        private LazyValue(java.util.function.Supplier<? extends T> supplier) {
            this.supplier = supplier;
        }

        private T get() {
            if (!initialized) {
                T next = supplier.get();
                value = next;
                initialized = true;
                supplier = null;
            }
            return value;
        }
    }
}
