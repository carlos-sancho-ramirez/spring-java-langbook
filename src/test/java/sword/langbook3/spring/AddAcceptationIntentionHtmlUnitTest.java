package sword.langbook3.spring;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

@SpringBootTest
public final class AddAcceptationIntentionHtmlUnitTest {

    @Autowired
    private WebApplicationContext context;

    @Test
    void test() throws IOException {
        final WebClient webClient = MockMvcWebClientBuilder
                .webAppContextSetup(context)
                .build();

        final WordEditorPage wordEditorPage = new WordEditorPage(webClient.getPage("http://localhost" + AddAcceptationIntentionController.URL));
        final SubmissionPage submissionPage = wordEditorPage.submitWord("mesa");
        submissionPage.submit();
    }

    record WordEditorPage(HtmlPage page) {

        SubmissionPage submitWord(@NonNull String text) throws IOException {
            final HtmlForm form = page.getHtmlElementById("stepForm");
            final HtmlTextInput input = page.getHtmlElementById("a2"); // Caution! a2 is subject to the database
            input.setValueAttribute(text);
            return new SubmissionPage(form.getOneHtmlElementByAttribute("input", "type", "submit").click());
        }
    }

    record SubmissionPage(HtmlPage page) {

        void submit() throws IOException {
            final HtmlForm form = page.getHtmlElementById("submitForm");
            form.getOneHtmlElementByAttribute("input", "type", "submit").click();
        }
    }
}
