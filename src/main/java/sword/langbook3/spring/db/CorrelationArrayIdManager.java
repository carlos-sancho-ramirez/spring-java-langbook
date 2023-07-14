package sword.langbook3.spring.db;

import sword.database.DbValue;
import sword.langbook3.android.db.IntSetter;

public final class CorrelationArrayIdManager implements IntSetter<CorrelationArrayId> {

    @Override
    public CorrelationArrayId getKeyFromInt(int key) {
        return new CorrelationArrayId(key);
    }

    @Override
    public CorrelationArrayId getKeyFromDbValue(DbValue value) {
        return getKeyFromInt(value.toInt());
    }
}
