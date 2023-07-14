package sword.langbook3.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sword.collections.ImmutableIntRange;
import sword.collections.ImmutableList;
import sword.database.DbQuery;
import sword.database.MemoryDatabase;
import sword.langbook3.android.db.ImmutableCorrelation;
import sword.langbook3.android.db.ImmutableCorrelationArray;
import sword.langbook3.android.models.SearchResult;
import sword.langbook3.spring.db.*;

@Controller
public final class MainSearchController {

    public final LangbookDbManagerImpl dbManager;

    private static void addSimpleAcceptation(LangbookDbManagerImpl dbManager, AlphabetId alphabet, String text) {
        final ImmutableCorrelation<AlphabetId> houseCorrelation = new ImmutableCorrelation.Builder<AlphabetId>()
                .put(alphabet, text)
                .build();

        final ImmutableCorrelationArray<AlphabetId> houseCorrelationArray = new ImmutableCorrelationArray.Builder<AlphabetId>()
                .append(houseCorrelation)
                .build();

        final ConceptId houseConcept = dbManager.getNextAvailableConceptId();
        dbManager.addAcceptation(houseConcept, houseCorrelationArray);
    }

    public MainSearchController() {
        dbManager = new LangbookDbManagerImpl(new MemoryDatabase());
        final AlphabetId alphabet = dbManager.addLanguage("es").mainAlphabet;
        addSimpleAcceptation(dbManager, alphabet, "casa");
        addSimpleAcceptation(dbManager, alphabet, "castillo");
        addSimpleAcceptation(dbManager, alphabet, "barraca");
    }

    @GetMapping("/")
    public String mainSearch(@RequestParam(name="query", required=false) String query, Model model) {
        if (query == null) {
            query = "";
        }

        final ImmutableList<SearchResult<AcceptationId, RuleId>> result = dbManager.findAcceptationFromText(query, DbQuery.RestrictionStringTypes.STARTS_WITH, new ImmutableIntRange(0, 19));
        final ImmutableList<String> texts = result.map(SearchResult::getStr);

        model.addAttribute("query", query);
        model.addAttribute("texts", texts);
        return "main_search";
    }
}
