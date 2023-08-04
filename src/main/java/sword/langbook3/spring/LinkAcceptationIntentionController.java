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
import sword.langbook3.spring.db.AlphabetId;
import sword.langbook3.spring.db.LangbookDbManagerImpl;
import sword.langbook3.spring.db.RuleId;

import java.util.Locale;
import java.util.Map;

@Controller
public final class LinkAcceptationIntentionController {

    public static final String PATH = "/intention/linkAcceptation";

    private interface LinkageMechanisms {
        String SHARE_CONCEPT = "s";
        String DUPLICATE_ACCEPTATION = "d";
    }

    interface ArgKeys {
        String LINKAGE_MECHANISM = "m";
        String NEW_ACCEPTATION_FLAG = "naf";
        String QUERY = "q";
        ArgKey<AcceptationId> SOURCE_ACCEPTATION_ID = new ArgKey<>("said");
        ArgKey<AcceptationId> TARGET_ACCEPTATION_ID = new ArgKey<>("taid");
    }

    public record HiddenDefinition(String id, String value) {
    }

    @GetMapping(PATH)
    public String get(@RequestParam Map<String, String> requestParams, Model model) {
        final ArgKeysMap args = new ArgKeysMap(requestParams);
        final AcceptationId sourceAccId = args.get(ArgKeys.SOURCE_ACCEPTATION_ID);
        if (sourceAccId == null) {
            return "bad_request";
        }

        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        final Locale locale = Locale.getDefault();

        final ImmutableList.Builder<HiddenDefinition> definitionsBuilder = new ImmutableList.Builder<HiddenDefinition>()
                .append(new HiddenDefinition(ArgKeys.SOURCE_ACCEPTATION_ID.key(), sourceAccId.toString()));

        final AcceptationId targetAccId = args.get(ArgKeys.TARGET_ACCEPTATION_ID);
        if (targetAccId == null) {
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

                model.addAttribute("q", query);
                model.addAttribute("acceptations", acceptations);
            }

            model.addAttribute("queryHref", PATH);
            model.addAttribute("definitions", definitionsBuilder.build());
            return "acceptation_picker";
        }
        else {
            definitionsBuilder.append(new HiddenDefinition(ArgKeys.TARGET_ACCEPTATION_ID.key(), targetAccId.toString()));
            model.addAttribute("definitions", definitionsBuilder.build());
            model.addAttribute("submitHref", PATH);

            final AlphabetId preferredAlphabet = LangbookApplication.getPreferredAlphabet();
            final LangbookDbManagerImpl dbManager = LangbookApplication.getDbManager();
            final String sourceText = dbManager.getAcceptationDisplayableText(sourceAccId, preferredAlphabet);
            final String targetText = dbManager.getAcceptationDisplayableText(targetAccId, preferredAlphabet);
            final String[] msgArgs = new String[] { sourceText, targetText };
            model.addAttribute("shareConceptDescription", messageSource.getMessage("linkageMechanismSelector.shareConceptDescription", msgArgs, locale));
            model.addAttribute("duplicateAcceptationDescription", messageSource.getMessage("linkageMechanismSelector.duplicateAcceptationDescription", msgArgs, locale));
            return "linkage_mechanism_selector";
        }
    }
}
