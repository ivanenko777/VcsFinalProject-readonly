package lt.ivl.webInternalApp.pdf;

import com.itextpdf.text.pdf.BaseFont;
import lt.ivl.components.domain.Repair;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Component
public class PdfGenerator {
    // LINKS
    // https://tuhrig.de/generating-pdfs-with-java-flying-saucer-and-thymeleaf/
    // https://github.com/tuhrig/Flying_Saucer_PDF_Generation/

    private static final String UTF_8 = "UTF-8";

    public void generateRepairConfirmedPdf(Repair repair, HttpServletResponse response) throws Exception {
        PdfDataConfirmedRepair data = new PdfDataConfirmedRepair(repair);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment;filename=confirmed-repair.pdf");
        OutputStream outputStream = response.getOutputStream();

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/pdf/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(UTF_8);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("data", data);

        String renderedHtmlContent = templateEngine.process("template_confirm", context);
        String xHtml = convertToXhtml(renderedHtmlContent);

        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont("/pdf/ARIALUNI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);


        renderer.setDocumentFromString(xHtml);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();

        response.flushBuffer();
    }

    private String convertToXhtml(String html) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding(UTF_8);
        tidy.setOutputEncoding(UTF_8);
        tidy.setXHTML(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
