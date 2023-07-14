package sword.langbook3.spring.db;

import sword.database.DbValue;
import sword.langbook3.android.db.BunchSetIntSetter;
import sword.langbook3.android.db.IntSetter;
import sword.langbook3.android.db.LangbookDbSchema;

public final class BunchSetIdManager implements BunchSetIntSetter<BunchSetId> {

    private final BunchSetId EMPTY = new BunchSetId(LangbookDbSchema.EMPTY_BUNCH_SET_ID);

    @Override
    public BunchSetId getKeyFromInt(int key) {
        return (key == LangbookDbSchema.EMPTY_BUNCH_SET_ID)? EMPTY : new BunchSetId(key);
    }

    @Override
    public BunchSetId getKeyFromDbValue(DbValue value) {
        return getKeyFromInt(value.toInt());
    }

    @Override
    public BunchSetId getDeclaredEmpty() {
        return EMPTY;
    }
}
