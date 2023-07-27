package sword.langbook3.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sword.collections.ImmutableList;
import sword.collections.ImmutableMap;
import sword.langbook3.android.models.DisplayableItem;
import sword.langbook3.spring.db.AlphabetId;
import sword.langbook3.spring.db.LanguageId;

import java.util.Map;

@Controller
public final class AddAcceptationIntentionController {

    @GetMapping("/intention/addAcceptation")
    public String step(@RequestParam Map<String, String> requestParams, Model model) {
        model.addAttribute("submitHref", "/intention/addAcceptation");
        model.addAttribute("selectorId", "l");

        final AlphabetId preferredAlphabet = LangbookApplication.getPreferredAlphabet();
        final ImmutableMap<LanguageId, String> languagesMap = LangbookApplication.getDbManager().readAllLanguages(preferredAlphabet);
        final ImmutableList<DisplayableItem<String>> options = languagesMap.keySet().map(langId ->
                new DisplayableItem<>(langId.toString(), languagesMap.get(langId)));
        model.addAttribute("options", options);
        return "language_picker";
    }
}
