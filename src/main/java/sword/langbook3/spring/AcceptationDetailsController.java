package sword.langbook3.spring;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sword.collections.ImmutableList;
import sword.langbook3.spring.Records.MenuItem;
import sword.langbook3.spring.db.AcceptationId;
import sword.langbook3.spring.db.AlphabetId;

import java.util.Locale;

@Controller
public final class AcceptationDetailsController {

    static final String PATH = "/acceptationDetails/";

    @GetMapping(PATH + "{accId}")
    public String getDetails(@PathVariable("accId") String accId, Model model) {
        final AlphabetId preferredAlphabet = LangbookApplication.getPreferredAlphabet();
        final var details = LangbookApplication.getDbManager().getAcceptationsDetails(new AcceptationId(Integer.parseInt(accId)), preferredAlphabet);

        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        final Locale locale = Locale.getDefault();

        final ImmutableList<MenuItem> menuItems = new ImmutableList.Builder<MenuItem>()
                .append(new MenuItem(LinkAcceptationIntentionController.PATH + "?" + LinkAcceptationIntentionController.ArgKeys.SOURCE_ACCEPTATION_ID + "=" + accId, messageSource.getMessage("acceptationDetails.linkAcceptation", null, locale)))
                .build();

        model.addAttribute("menuItems", menuItems);
        model.addAttribute("accId", accId);
        model.addAttribute("accText", details.getTexts().reduce((a, b) -> a + b, ""));
        return "acceptation_details";
    }
}
