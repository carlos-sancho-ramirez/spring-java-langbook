package sword.langbook3.spring;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sword.collections.*;
import sword.langbook3.android.db.ImmutableCorrelation;
import sword.langbook3.android.db.ImmutableCorrelationArray;
import sword.langbook3.android.models.DisplayableItem;
import sword.langbook3.android.models.LanguageCreationResult;
import sword.langbook3.spring.AddLanguageIntentionController.NamedItem;
import sword.langbook3.spring.db.*;

import java.util.Locale;
import java.util.Map;

import static sword.langbook3.spring.AddLanguageIntentionController.*;

@Controller
public final class AddAcceptationIntentionController {

    private static final String URL = "/intention/addAcceptation";

    private interface ArgKeys {
        String ALPHABET_PREFIX = "a";
        String CORRELATION_ARRAY = "c";
        String LANGUAGE = "l";
    }

    public record Definition(String id, String label, String value, String displayedValue) {
    }

    @GetMapping(URL)
    public String get(@RequestParam Map<String, String> requestParams, Model model) {
        model.addAttribute("submitHref", "/intention/addAcceptation");

        final AlphabetId preferredAlphabet = LangbookApplication.getPreferredAlphabet();
        final LangbookDbManagerImpl dbManager = LangbookApplication.getDbManager();
        final ImmutableMap<LanguageId, String> languagesMap = dbManager.readAllLanguages(preferredAlphabet);

        LanguageId languageId = LanguageId.from(requestParams.get(ArgKeys.LANGUAGE));
        if (languageId == null) {
            final LanguageId uniqueLanguage = dbManager.getUniqueLanguage();
            if (uniqueLanguage != null) {
                languageId = uniqueLanguage;
            }
        }

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
            final ImmutableList.Builder<Definition> definitionsBuilder = new ImmutableList.Builder<Definition>()
                    .append(new Definition(ArgKeys.LANGUAGE, messageSource.getMessage("concept.language", null, locale), languageId.toString(), languagesMap.get(languageId)));

            final boolean correlationArrayPresent = requestParams.containsKey(ArgKeys.CORRELATION_ARRAY);
            final ImmutableMap<AlphabetId, String> alphabetMap = dbManager.readAlphabetsForLanguage(languageId, preferredAlphabet);
            if (!correlationArrayPresent && !alphabetMap.keySet().allMatch(id -> requestParams.containsKey(ArgKeys.ALPHABET_PREFIX + id))) {
                model.addAttribute("definitions", definitionsBuilder.build());

                final ImmutableList<NamedItem> requiredItems = alphabetMap.keySet().map(id -> new NamedItem(ArgKeys.ALPHABET_PREFIX + id, alphabetMap.get(id), null));
                model.addAttribute("requiredItems", requiredItems);

                return "word_editor";
            }
            else {
                ImmutableCorrelation<String> correlation = ImmutableCorrelation.empty();
                for (String alphabetId : alphabetMap.keySet().map(AlphabetId::toString)) {
                    correlation = correlation.put(alphabetId, requestParams.get(ArgKeys.ALPHABET_PREFIX + alphabetId));
                }

                final ImmutableSet<ImmutableCorrelationArray<String>> rawOptions = correlation.checkPossibleCorrelationArrays(SortUtils::compareCharSequenceByUnicode);
                String encodedCorrelationArray = requestParams.get(ArgKeys.CORRELATION_ARRAY);
                if (encodedCorrelationArray == null && rawOptions.size() == 1) {
                    encodedCorrelationArray = composeId(rawOptions.valueAt(0));
                }

                if (encodedCorrelationArray == null) {
                    final ImmutableList<AddLanguageIntentionController.DisplayableItem> options = rawOptions
                            .map(opt -> new AddLanguageIntentionController.DisplayableItem(composeId(opt), composeText(opt)));

                    model.addAttribute("definitions", definitionsBuilder.build());
                    model.addAttribute("selectorId", ArgKeys.CORRELATION_ARRAY);
                    model.addAttribute("options", options);
                    return "correlation_array_picker";
                }
                else {
                    final String displayableCorrelationArray = composeTextFromEncoded(alphabetMap.keySet().toList(), encodedCorrelationArray);
                    definitionsBuilder.append(new Definition(ArgKeys.CORRELATION_ARRAY, messageSource.getMessage("concept.correlationArray", null, locale), encodedCorrelationArray, displayableCorrelationArray));

                    model.addAttribute("definitions", definitionsBuilder.build());
                    model.addAttribute("submitText", messageSource.getMessage("mainSearch.addAcceptation", null, locale));
                    return "submission";
                }
            }
        }
    }

    @PostMapping(URL)
    public String post(@RequestParam Map<String, String> requestBody) {
        // TODO: We should check that all required parameters are present and valid before touching the database

        final LangbookDbManagerImpl dbManager = LangbookApplication.getDbManager();

        final LanguageId languageId = LanguageId.from(requestBody.get(ArgKeys.LANGUAGE));
        final ImmutableList<AlphabetId> alphabetIds = dbManager.findAlphabetsByLanguage(languageId).toList();
        final ImmutableCorrelationArray<AlphabetId> correlationArray = decodeCorrelationArray(alphabetIds, requestBody.get(ArgKeys.CORRELATION_ARRAY));

        final ConceptId concept = dbManager.getNextAvailableConceptId();
        final AcceptationId acceptation = dbManager.addAcceptation(concept, correlationArray);

        if (acceptation == null) {
            return "bad_request";
        }
        else {
            return "redirect:" + AcceptationDetailsController.PATH + acceptation;
        }
    }
}
