package sword.langbook3.spring.db;

import sword.database.DbIdentifiableQueryBuilder;
import sword.database.DbSettableQueryBuilder;
import sword.database.DbValue;
import sword.langbook3.android.db.SymbolArrayIdInterface;

public final class SymbolArrayId implements SymbolArrayIdInterface {

    final int key;

    SymbolArrayId(int key) {
        if (key == 0) {
            throw new IllegalArgumentException();
        }

        this.key = key;
    }

    @Override
    public boolean sameValue(DbValue value) {
        return value.toInt() == key;
    }

    @Override
    public void where(int columnIndex, DbIdentifiableQueryBuilder builder) {
        builder.where(columnIndex, key);
    }

    @Override
    public void put(int columnIndex, DbSettableQueryBuilder builder) {
        builder.put(columnIndex, key);
    }

    @Override
    public int hashCode() {
        return key;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SymbolArrayId && ((SymbolArrayId) other).key == key;
    }

    @Override
    public String toString() {
        return Integer.toString(key);
    }
}
