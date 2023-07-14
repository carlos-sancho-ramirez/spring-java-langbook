package sword.langbook3.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sword.collections.ImmutableIntRange;
import sword.collections.ImmutableList;
import sword.collections.ImmutableMap;
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
                // TODO: We should jump to the correlation picker for the language
                return "main_search";
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
