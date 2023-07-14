package sword.langbook3.spring;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public final class MainSearchController {
    @GetMapping("/")
    public String mainSearch() {
        return "main_search";
    }
}
