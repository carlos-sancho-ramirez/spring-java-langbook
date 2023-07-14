package sword.langbook3.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sword.collections.ImmutableList;
import sword.collections.ImmutableMap;
import sword.langbook3.spring.db.AlphabetId;
import sword.langbook3.spring.db.LangbookDbManagerImpl;
import sword.langbook3.spring.db.LanguageId;

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
    public String addLanguage(@RequestParam(name = "languageCode", required = false) String languageCode, @RequestParam(name = "alphabetCount", required = false) String alphabetCount, Model model) {
        if (isValidLanguageCode(languageCode) && isValidAlphabetCount(alphabetCount)) {
            // TODO: This should point to the second step of this intention
            return "main_search";
        }
        else {
            model.addAttribute("languageCode", languageCode);
            model.addAttribute("alphabetCount", alphabetCount);
            return "add_language";
        }
    }
}
