package pccth.code.review.Backend.Service;

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Component
public class HtmlToPdfConverter {

    private static final String FONT_FAMILY = "Sarabun";
    private static final String FONT_REGULAR = "/fonts/Sarabun-Regular.ttf";
    private static final String FONT_BOLD = "/fonts/Sarabun-Bold.ttf";

    public byte[] convert(String html) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            org.w3c.dom.Document document = new W3CDom().fromJsoup(Jsoup.parse(html));

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withW3cDocument(document, "/");

            builder.useFont(() -> resource(FONT_REGULAR), FONT_FAMILY, 400, FontStyle.NORMAL, true);
            builder.useFont(() -> resource(FONT_BOLD), FONT_FAMILY, 700, FontStyle.NORMAL, true);

            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to render PDF from HTML", e);
        }
    }

    private InputStream resource(String path) {
        InputStream in = getClass().getResourceAsStream(path);
        if (in == null) {
            throw new IllegalStateException("Required font resource not found on classpath: " + path);
        }
        return in;
    }
}
