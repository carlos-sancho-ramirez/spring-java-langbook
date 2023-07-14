package sword.langbook3.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sword.collections.ImmutableList;
import sword.collections.ImmutableMap;
import sword.langbook3.spring.db.AlphabetId;
import sword.langbook3.spring.db.LangbookDbManagerImpl;
import sword.langbook3.spring.db.LanguageId;

@Controller
public final class AlphabetsController {

    @GetMapping("/alphabets")
    public String checkAlphabets(Model model) {
        final AlphabetId preferredAlphabet = LangbookApplication.getPreferredAlphabet();
        final LangbookDbManagerImpl dbManager = LangbookApplication.getDbManager();
        final ImmutableMap<LanguageId, String> languageTexts = dbManager.readAllLanguages(preferredAlphabet);
        final ImmutableList<Language> languages = languageTexts.keySet().map(langId -> {
            final ImmutableMap<AlphabetId, String> rawAlphabets = dbManager.readAlphabetsForLanguage(langId, preferredAlphabet);
            final ImmutableList<Alphabet> alphabets = rawAlphabets.keySet().map(alpId -> new Alphabet(alpId.toString(), rawAlphabets.get(alpId)));
            return new Language(langId.toString(), languageTexts.get(langId), alphabets);
        });
        model.addAttribute("languages", languages);
        return "alphabets";
    }

    public record Language(String id, String text, ImmutableList<Alphabet> alphabets) {
    }

    public record Alphabet(String id, String text) {
    }
}
