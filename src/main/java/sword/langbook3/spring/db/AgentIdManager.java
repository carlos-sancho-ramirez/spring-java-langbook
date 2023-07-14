package sword.langbook3.spring.db;

import sword.database.DbValue;
import sword.langbook3.android.db.IntSetter;

public final class AgentIdManager implements IntSetter<AgentId> {

    @Override
    public AgentId getKeyFromInt(int key) {
        return (key != 0)? new AgentId(key) : null;
    }

    @Override
    public AgentId getKeyFromDbValue(DbValue value) {
        return getKeyFromInt(value.toInt());
    }
}
