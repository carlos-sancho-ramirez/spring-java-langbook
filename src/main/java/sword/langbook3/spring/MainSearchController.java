package sword.langbook3.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sword.collections.ImmutableIntRange;
import sword.collections.ImmutableList;
import sword.database.DbQuery;
import sword.langbook3.android.models.SearchResult;
import sword.langbook3.spring.db.*;

@Controller
public final class MainSearchController {

    @GetMapping("/")
    public String mainSearch(@RequestParam(name="query", required=false) String query, Model model) {
        if (query == null) {
            query = "";
        }

        final ImmutableList<SearchResult<AcceptationId, RuleId>> result = LangbookApplication.getDbManager().findAcceptationFromText(query, DbQuery.RestrictionStringTypes.STARTS_WITH, new ImmutableIntRange(0, 19));
        final ImmutableList<Acceptation> acceptations = result.map(r -> new Acceptation(r.getId().toString(), r.getStr()));

        model.addAttribute("query", query);
        model.addAttribute("acceptations", acceptations);
        return "main_search";
    }

    public record Acceptation(String id, String text) {
    }
}
