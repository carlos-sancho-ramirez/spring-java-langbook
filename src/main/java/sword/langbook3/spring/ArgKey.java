package sword.langbook3.spring;

import org.springframework.lang.NonNull;

import static sword.langbook3.android.util.PreconditionUtils.ensureNonNull;

public record ArgKey<T>(@NonNull String key) {

    public ArgKey {
        ensureNonNull(key);
    }

    @NonNull
    @Override
    public String toString() {
        return key;
    }
}
