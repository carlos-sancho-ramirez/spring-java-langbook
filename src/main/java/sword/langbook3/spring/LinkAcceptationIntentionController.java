package sword.langbook3.spring;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sword.collections.ImmutableIntRange;
import sword.collections.ImmutableList;
import sword.database.DbQuery;
import sword.langbook3.android.models.SearchResult;
import sword.langbook3.spring.Records.MenuItem;
import sword.langbook3.spring.db.AcceptationId;
import sword.langbook3.spring.db.RuleId;

import java.util.Locale;
import java.util.Map;

@Controller
public final class LinkAcceptationIntentionController {

    public static final String PATH = "/intention/linkAcceptation";

    interface ArgKeys {
        String NEW_ACCEPTATION_FLAG = "naf";
        String QUERY = "q";
        String SOURCE_ACCEPTATION_ID = "said";
        String TARGET_ACCEPTATION_ID = "taid";
    }

    public record HiddenDefinition(String id, String value) {
    }

    @GetMapping(PATH)
    public String get(@RequestParam Map<String, String> requestParams, Model model) {
        final String sourceAccId = requestParams.get(ArgKeys.SOURCE_ACCEPTATION_ID);
        if (sourceAccId == null) {
            return "bad_request";
        }

        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        final Locale locale = Locale.getDefault();

        final ImmutableList<MenuItem> menuItems = new ImmutableList.Builder<MenuItem>()
                .append(new MenuItem(PATH + "?" + ArgKeys.SOURCE_ACCEPTATION_ID + "=" + sourceAccId + "&" + ArgKeys.NEW_ACCEPTATION_FLAG, messageSource.getMessage("mainSearch.addAcceptation", null, locale)))
                .build();
        model.addAttribute("menuItems", menuItems);

        final String query = requestParams.get(ArgKeys.QUERY);
        if (query != null) {
            final ImmutableList<SearchResult<AcceptationId, RuleId>> result = LangbookApplication.getDbManager().findAcceptationFromText(query, DbQuery.RestrictionStringTypes.STARTS_WITH, new ImmutableIntRange(0, 19));
            final ImmutableList<MenuItem> acceptations = result.map(r -> {
                final String href = PATH + "?" + ArgKeys.SOURCE_ACCEPTATION_ID + "=" + sourceAccId + "&" + ArgKeys.TARGET_ACCEPTATION_ID + "=" + r.getId().toString();
                return new MenuItem(href, r.getStr());
            });

            System.out.println("Acceptations: [" + acceptations.map(Object::toString).reduce((a, b) -> a + ", " + b, "") + "]");
            model.addAttribute("q", query);
            model.addAttribute("acceptations", acceptations);
        }

        model.addAttribute("queryHref", PATH);

        final ImmutableList<HiddenDefinition> definitions = new ImmutableList.Builder<HiddenDefinition>()
                .append(new HiddenDefinition(ArgKeys.SOURCE_ACCEPTATION_ID, sourceAccId))
                .build();

        model.addAttribute("definitions", definitions);
        return "acceptation_picker";
    }
}
