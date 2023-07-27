package sword.langbook3.spring;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sword.collections.*;
import sword.langbook3.android.db.ImmutableCorrelation;
import sword.langbook3.android.db.ImmutableCorrelationArray;
import sword.langbook3.android.models.LanguageCreationResult;
import sword.langbook3.spring.db.*;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

@Controller
public final class AddLanguageIntentionController {

    private boolean isValidCodeCharacter(char ch) {
        return ch >= 'a' && ch <= 'z';
    }

    private boolean isValidLanguageCode(String code) {
        return code != null && code.length() == 2 && isValidCodeCharacter(code.charAt(0)) && isValidCodeCharacter(code.charAt(1));
    }

    private boolean isValidAlphabetCount(String alphabetCount) {
        if (alphabetCount == null) {
            return false;
        }

        try {
            return Integer.parseInt(alphabetCount) > 0;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public record DisplayableItem(String id, String text) {
    }

    public record NamedItem(String id, String label, String text) {
    }

    private static final char ENCODED_ARRAY_SEPARATOR = '.';

    static String composeId(ImmutableCorrelationArray<String> option) {
        // Currently this will fail if texts include separator
        // TODO: Changing this logic to allow the separator characters
        return option
                .map(corr -> corr.sort(SortUtils::compareCharSequenceByUnicode).reduce((a, b) -> a + ENCODED_ARRAY_SEPARATOR + b))
                .reduce((a, b) -> a + ENCODED_ARRAY_SEPARATOR + b);
    }

    static String composeText(ImmutableCorrelationArray<String> option) {
        // Currently this will fail if texts include ','
        // TODO: Changing this logic to allow the separator characters
        return option
                .map(corr -> corr.sort(SortUtils::compareCharSequenceByUnicode).reduce((a, b) -> a + '/' + b))
                .reduce((a, b) -> a + " + " + b);
    }

    private ImmutableCorrelationArray<AlphabetId> decodeCorrelationArray(List<AlphabetId> alphabetIds, String encoded) {
        final String[] parts = encoded.split(Pattern.quote("" + ENCODED_ARRAY_SEPARATOR));
        if (parts.length == 0 || parts.length % alphabetIds.size() != 0) {
            throw new IllegalArgumentException();
        }

        final ImmutableCorrelationArray.Builder<AlphabetId> builder = new ImmutableCorrelationArray.Builder<>();
        int partIndex = 0;
        while (partIndex < parts.length) {
            ImmutableCorrelation<AlphabetId> correlation = ImmutableCorrelation.empty();
            for (AlphabetId alphabetId : alphabetIds) {
                correlation = correlation.put(alphabetId, parts[partIndex++]);
            }
            builder.add(correlation);
        }

        return builder.build();
    }

    @GetMapping("/intention/addLanguage")
    public String addLanguage(@RequestParam Map<String, String> requestParams, Model model) {
        final String languageCode = requestParams.get("languageCode");
        final String alphabetCountStr = requestParams.get("alphabetCount");
        model.addAttribute("languageCode", languageCode);
        model.addAttribute("alphabetCount", alphabetCountStr);

        if (isValidLanguageCode(languageCode) && isValidAlphabetCount(alphabetCountStr)) {
            final int alphabetCount = Integer.parseInt(alphabetCountStr);
            final ImmutableList<String> alphabetIds = new ImmutableIntRange(1, alphabetCount).map(i -> "a" + i);

            final ImmutableList<String> definedTexts = alphabetIds.map(id -> requestParams.get("d" + id));
            final boolean definedTextsAreValid = definedTexts.allMatch(t -> t != null && t.length() > 0);
            final ImmutableSet<ImmutableCorrelationArray<String>> rawOptions;
            if (definedTextsAreValid) {
                ImmutableCorrelation<String> correlation = ImmutableCorrelation.empty();
                for (String alphabetId : alphabetIds) {
                    correlation = correlation.put(alphabetId, requestParams.get("d" + alphabetId));
                }

                rawOptions = correlation.checkPossibleCorrelationArrays(SortUtils::compareCharSequenceByUnicode);
            }
            else {
                rawOptions = ImmutableHashSet.empty();
            }

            String defining = null;
            boolean definedTextsConsumed = false;
            final MutableList<NamedItem> definitions = MutableList.empty();
            final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
            messageSource.setBasename("messages");
            messageSource.setDefaultEncoding("UTF-8");
            final Locale locale = Locale.getDefault();

            if (requestParams.containsKey("l")) {
                final String concept = messageSource.getMessage("concept.language", null, locale);
                final String label = messageSource.getMessage("addLanguage.requiredItemLabel", new Object[] { concept }, locale);
                definitions.append(new NamedItem("l", label, requestParams.get("l")));
            }
            else if (rawOptions.size() == 1) {
                final String concept = messageSource.getMessage("concept.language", null, locale);
                final String label = messageSource.getMessage("addLanguage.requiredItemLabel", new Object[] { concept }, locale);
                definitions.append(new NamedItem("l", label, composeId(rawOptions.valueAt(0))));
                definedTextsConsumed = true;
                defining = "a1";
            }
            else {
                defining = "l";
            }

            for (int alphabetIndex : alphabetIds.indexes()) {
                final String alphabetId = alphabetIds.valueAt(alphabetIndex);
                if (requestParams.containsKey(alphabetId)) {
                    final String index = alphabetId.substring(1);
                    final String concept = messageSource.getMessage("concept.alphabet", null, locale) + " " + index;
                    final String label = messageSource.getMessage("addLanguage.requiredItemLabel", new Object[] { concept }, locale);
                    definitions.append(new NamedItem(alphabetId, label, requestParams.get(alphabetId)));
                }
                else if (defining == null && rawOptions.size() == 1) {
                    final String index = alphabetId.substring(1);
                    final String concept = messageSource.getMessage("concept.alphabet", null, locale) + " " + index;
                    final String label = messageSource.getMessage("addLanguage.requiredItemLabel", new Object[] { concept }, locale);
                    definitions.append(new NamedItem(alphabetId, label, composeId(rawOptions.valueAt(0))));
                    definedTextsConsumed = true;
                    if (alphabetIndex + 1 < alphabetIds.size()) {
                        defining = alphabetIds.valueAt(alphabetIndex + 1);
                    }
                }
                else if (defining == null) {
                    defining = alphabetId;
                }
            }
            model.addAttribute("definitions", definitions.toImmutable());

            if (defining == null) {
                return "add_language_submission";
            }
            else {
                if (definedTextsAreValid && !definedTextsConsumed) {
                    final ImmutableList<DisplayableItem> options = rawOptions
                            .map(opt -> new DisplayableItem(composeId(opt), composeText(opt)));
                    model.addAttribute("defining", defining);
                    model.addAttribute("correlations", options);
                    return "add_language_language_correlation_picker";
                } else {
                    final boolean isDefiningLanguage = "l".equals(defining);
                    final ImmutableList<NamedItem> requiredItems = alphabetIds.map(alphabetId -> {
                        final String id = "d" + alphabetId;
                        final String index = alphabetId.substring(1);
                        final String conceptType = messageSource.getMessage(isDefiningLanguage? "concept.language" : "concept.alphabet", null, locale);
                        final String concept = isDefiningLanguage? conceptType : conceptType + " " + index;
                        final String label = messageSource.getMessage("addLanguage.requiredItemLabel", new Object[] { concept }, locale);
                        return new NamedItem(id, label, null);
                    });
                    model.addAttribute("requiredItems", requiredItems);
                    return "add_language_language_edition";
                }
            }
        }
        else {
            return "add_language";
        }
    }

    @PostMapping("/intention/addLanguage")
    public String postLanguage(@RequestParam Map<String, String> requestBody) {
        // TODO: We should check that all required parameters are present and valid before touching the database

        final LangbookDbManagerImpl dbManager = LangbookApplication.getDbManager();
        final LanguageCreationResult<LanguageId, AlphabetId> languageResult = dbManager.addLanguage(requestBody.get("languageCode"));

        final MutableList<AlphabetId> alphabetIds = MutableList.empty();
        alphabetIds.append(languageResult.mainAlphabet);

        final int alphabetCount = Integer.parseInt(requestBody.get("alphabetCount"));
        for (int i = 1; i < alphabetCount; i++) {
            final AlphabetId newAlphabet = AlphabetIdManager.conceptAsAlphabetId(dbManager.getNextAvailableConceptId());
            alphabetIds.append(newAlphabet);
            if (!dbManager.addAlphabetCopyingFromOther(newAlphabet, languageResult.mainAlphabet)) {
                throw new AssertionError();
            }
        }

        dbManager.addAcceptation(languageResult.language.getConceptId(), decodeCorrelationArray(alphabetIds, requestBody.get("l")));
        int alphabetIndex = 0;
        for (AlphabetId alphabetId : alphabetIds) {
            final String paramId = "a" + (++alphabetIndex);
            final String encoded = requestBody.get(paramId);
            if (dbManager.addAcceptation(alphabetId.getConceptId(), decodeCorrelationArray(alphabetIds, encoded)) == null) {
                throw new AssertionError();
            }
        }

        return "redirect:/alphabets";
    }

}
