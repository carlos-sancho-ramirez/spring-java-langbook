package sword.langbook3.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sword.collections.*;
import sword.langbook3.android.db.ImmutableCorrelation;
import sword.langbook3.android.db.ImmutableCorrelationArray;
import sword.langbook3.spring.db.AlphabetId;
import sword.langbook3.spring.db.LangbookDbManagerImpl;
import sword.langbook3.spring.db.LanguageId;

import java.util.Map;

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

    public record CorrelationArrayOption(String id, String text) {
    }

    private static final char COMPOSED_ID_SEPARATOR = '.';

    private String composeId(ImmutableCorrelationArray<String> option) {
        // Currently this will fail if texts include separator
        // TODO: Changing this logic to allow the separator characters
        return option
                .map(corr -> corr.sort(SortUtils::compareCharSequenceByUnicode).reduce((a, b) -> a + COMPOSED_ID_SEPARATOR + b))
                .reduce((a, b) -> a + COMPOSED_ID_SEPARATOR + b);
    }

    private String composeText(ImmutableCorrelationArray<String> option) {
        // Currently this will fail if texts include ','
        // TODO: Changing this logic to allow the separator characters
        return option
                .map(corr -> corr.sort(SortUtils::compareCharSequenceByUnicode).reduce((a, b) -> a + '/' + b))
                .reduce((a, b) -> a + " + " + b);
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

            final ImmutableList<String> languageTexts = alphabetIds.map(id -> requestParams.get("l" + id));
            if (languageTexts.allMatch(t -> t != null && t.length() > 0)) {
                ImmutableCorrelation<String> correlation = ImmutableCorrelation.empty();
                for (String alphabetId : alphabetIds) {
                    correlation = correlation.put(alphabetId, requestParams.get("l" + alphabetId));
                }

                final ImmutableList<CorrelationArrayOption> options = correlation
                        .checkPossibleCorrelationArrays(SortUtils::compareCharSequenceByUnicode)
                        .map(opt -> new CorrelationArrayOption(composeId(opt), composeText(opt)));
                model.addAttribute("correlations", options);
                return "add_language_language_correlation_picker";
            }
            else {
                model.addAttribute("languageId", "l");
                model.addAttribute("alphabetIds", alphabetIds);
                return "add_language_language_edition";
            }
        }
        else {
            return "add_language";
        }
    }
}
