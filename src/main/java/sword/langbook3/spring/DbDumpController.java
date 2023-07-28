package sword.langbook3.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sword.collections.ImmutableList;
import sword.collections.MutableList;
import sword.database.DbColumn;
import sword.database.DbExporter.Database;
import sword.database.DbQuery;
import sword.database.DbResult;
import sword.database.DbValue;
import sword.langbook3.android.db.LangbookDbSchema.Tables;

@Controller
public final class DbDumpController {

    public record TableDump(String name, ImmutableList<String> columnNames, ImmutableList<ImmutableList<String>> content) {
    }

    private void dumpAcceptations(Database db, MutableList<TableDump> result) {
        final DbResult dbResult = db.select(new DbQuery.Builder(Tables.acceptations).select(
                Tables.acceptations.getIdColumnIndex(),
                Tables.acceptations.getConceptColumnIndex(),
                Tables.acceptations.getCorrelationArrayColumnIndex()));

        final int expectedSize = dbResult.getRemainingRows();
        final ImmutableList.Builder<ImmutableList<String>> builder = new ImmutableList.Builder<>((currentSize, newSize) -> Math.max(newSize, expectedSize));
        while (dbResult.hasNext()) {
            builder.append(dbResult.next().map(DbValue::toText).toImmutable());
        }

        result.append(new TableDump(Tables.acceptations.name(), Tables.acceptations.columns().map(DbColumn::name), builder.build()));
    }

    @GetMapping("/dump")
    public String dump(Model model) {
        final Database db = LangbookApplication.getDatabase();
        final MutableList<TableDump> result = MutableList.empty();
        dumpAcceptations(db, result);
        model.addAttribute("content", result.toImmutable());
        return "dump";
    }
}
