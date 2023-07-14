package sword.langbook3.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sword.langbook3.spring.db.AcceptationId;
import sword.langbook3.spring.db.AlphabetId;

@Controller
public final class AcceptationDetailsController {

    @GetMapping("/details/{accId}")
    public String getDetails(@PathVariable("accId") String accId, Model model) {
        final AlphabetId preferredAlphabet = LangbookApplication.getPreferredAlphabet();
        final var details = LangbookApplication.getDbManager().getAcceptationsDetails(new AcceptationId(Integer.parseInt(accId)), preferredAlphabet);
        model.addAttribute("accId", accId);
        model.addAttribute("accText", details.getTexts().reduce((a, b) -> a + b, ""));
        return "details";
    }
}
