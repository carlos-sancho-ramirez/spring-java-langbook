package sword.langbook3.spring.db;

import sword.database.DbValue;
import sword.langbook3.android.db.IntSetter;

public final class CorrelationIdManager implements IntSetter<CorrelationId> {

    @Override
    public CorrelationId getKeyFromInt(int key) {
        return new CorrelationId(key);
    }

    @Override
    public CorrelationId getKeyFromDbValue(DbValue value) {
        return getKeyFromInt(value.toInt());
    }
}
