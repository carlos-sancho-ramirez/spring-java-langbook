package sword.langbook3.spring;

import org.springframework.lang.NonNull;
import sword.langbook3.spring.db.AcceptationId;

import java.util.Map;

import static sword.langbook3.android.util.PreconditionUtils.ensureNonNull;

public final class ArgKeysMap {

    @NonNull
    private final Map<String, String> map;

    public ArgKeysMap(@NonNull Map<String, String> map) {
        ensureNonNull(map);
        this.map = map;
    }

    public AcceptationId get(@NonNull ArgKey<AcceptationId> key) {
        final String value = map.get(key.key());
        return (value == null)? null : AcceptationId.from(value);
    }
}
