package sword.langbook3.spring;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sword.collections.ImmutableList;
import sword.collections.ImmutableMap;
import sword.langbook3.android.models.DisplayableItem;
import sword.langbook3.spring.AddLanguageIntentionController.NamedItem;
import sword.langbook3.spring.db.AlphabetId;
import sword.langbook3.spring.db.LangbookDbManagerImpl;
import sword.langbook3.spring.db.LanguageId;

import java.util.Locale;
import java.util.Map;

@Controller
public final class AddAcceptationIntentionController {

    private interface ArgKeys {
        String ALPHABET_PREFIX = "a";
        String LANGUAGE = "l";
    }

    @GetMapping("/intention/addAcceptation")
    public String step(@RequestParam Map<String, String> requestParams, Model model) {
        model.addAttribute("submitHref", "/intention/addAcceptation");

        final AlphabetId preferredAlphabet = LangbookApplication.getPreferredAlphabet();
        final LangbookDbManagerImpl dbManager = LangbookApplication.getDbManager();
        final ImmutableMap<LanguageId, String> languagesMap = dbManager.readAllLanguages(preferredAlphabet);

        final LanguageId languageId = LanguageId.from(requestParams.get(ArgKeys.LANGUAGE));
        if (languageId == null) {
            model.addAttribute("selectorId", ArgKeys.LANGUAGE);

            final ImmutableList<DisplayableItem<String>> options = languagesMap.keySet().map(langId ->
                    new DisplayableItem<>(langId.toString(), languagesMap.get(langId)));
            model.addAttribute("options", options);
            return "language_picker";
        }
        else {
            final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
            messageSource.setBasename("messages");
            messageSource.setDefaultEncoding("UTF-8");
            final Locale locale = Locale.getDefault();

            final ImmutableList<NamedItem> definitions = new ImmutableList.Builder<NamedItem>()
                    .append(new NamedItem(ArgKeys.LANGUAGE, messageSource.getMessage("concept.language", null, locale), languageId.toString()))
                    .build();
            model.addAttribute("definitions", definitions);

            final ImmutableMap<AlphabetId, String> alphabetMap = dbManager.readAlphabetsForLanguage(languageId, preferredAlphabet);
            final ImmutableList<NamedItem> requiredItems = alphabetMap.keySet().map(id -> new NamedItem(ArgKeys.ALPHABET_PREFIX + id.toString(), alphabetMap.get(id), null));
            model.addAttribute("requiredItems", requiredItems);

            return "word_editor";
        }
    }
}
