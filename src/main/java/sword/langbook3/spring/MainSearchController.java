package sword.langbook3.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sword.collections.ImmutableList;

@Controller
public final class MainSearchController {
    @GetMapping("/")
    public String mainSearch(@RequestParam(name="query", required=false) String query, Model model) {
        final ImmutableList<String> texts = new ImmutableList.Builder<String>()
                .append("a")
                .append("b")
                .append("c")
                .build();

        model.addAttribute("query", query);
        model.addAttribute("texts", texts);
        return "main_search";
    }
}
