package sword.langbook3.spring.db;

import sword.database.DbValue;
import sword.langbook3.android.db.IntSetter;

public final class SymbolArrayIdManager implements IntSetter<SymbolArrayId> {

    @Override
    public SymbolArrayId getKeyFromInt(int key) {
        return (key != 0)? new SymbolArrayId(key) : null;
    }

    @Override
    public SymbolArrayId getKeyFromDbValue(DbValue value) {
        return getKeyFromInt(value.toInt());
    }
}
