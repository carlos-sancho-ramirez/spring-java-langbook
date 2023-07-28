package sword.langbook3.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sword.collections.ImmutableList;
import sword.collections.MutableList;
import sword.database.*;
import sword.database.DbExporter.Database;
import sword.langbook3.android.db.LangbookDbSchema.Tables;

@Controller
public final class DbDumpController {

    public record TableDump(String name, ImmutableList<String> columnNames, ImmutableList<ImmutableList<String>> content) {
    }

    private void dump(Database db, DbTable table, MutableList<TableDump> result) {
        final int columnCount = table.columns().size();
        final int[] selection = new int[columnCount];
        for (int i = 0; i < columnCount; i++) {
            selection[i] = i;
        }

        final DbResult dbResult = db.select(new DbQuery.Builder(table).select(selection));
        final int expectedSize = dbResult.getRemainingRows();
        final ImmutableList.Builder<ImmutableList<String>> builder = new ImmutableList.Builder<>((currentSize, newSize) -> Math.max(newSize, expectedSize));
        while (dbResult.hasNext()) {
            builder.append(dbResult.next().map(DbValue::toText).toImmutable());
        }

        result.append(new TableDump(table.name(), table.columns().map(DbColumn::name), builder.build()));
    }

    @GetMapping("/dump")
    public String dump(Model model) {
        final Database db = LangbookApplication.getDatabase();
        final MutableList<TableDump> result = MutableList.empty();
        dump(db, Tables.languages, result);
        dump(db, Tables.alphabets, result);
        dump(db, Tables.symbolArrays, result);
        dump(db, Tables.correlations, result);
        dump(db, Tables.correlationArrays, result);
        dump(db, Tables.acceptations, result);
        dump(db, Tables.stringQueries, result);
        model.addAttribute("content", result.toImmutable());
        return "dump";
    }
}
