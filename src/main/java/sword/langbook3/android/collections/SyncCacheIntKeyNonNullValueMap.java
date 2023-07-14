package sword.langbook3.android.collections;

import sword.collections.IntFunction;
import sword.collections.IntKeyMapGetter;
import sword.collections.MutableIntKeyMap;

public final class SyncCacheIntKeyNonNullValueMap<T> implements IntKeyMapGetter<T> {

    private final T noFound = null;
    private final IntFunction<? extends T> _supplier;
    private final MutableIntKeyMap<T> _map;

    public SyncCacheIntKeyNonNullValueMap(IntFunction<? extends T> supplier) {
        _supplier = supplier;
        _map = MutableIntKeyMap.empty();
    }

    public SyncCacheIntKeyNonNullValueMap(MutableIntKeyMap<T> map, IntFunction<? extends T> supplier) {
        _supplier = supplier;
        _map = map;
    }

    @Override
    public T get(int key) {
        // This implementation avoid explicitly using _map.keySet() as it is a really slow method. get with default value is used instead.
        // However, this limits that the supplier cannot return the noFound reference as it is reserved.
        // TODO: Removing the noFound restriction once the collections library provide a proper solution for this
        T value = _map.get(key, noFound);
        if (value == noFound) {
            value = _supplier.apply(key);
            if (value == noFound) {
                throw new UnsupportedOperationException("Invalid return for supplier function");
            }
            _map.put(key, value);
        }

        return value;
    }
}
