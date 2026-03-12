package fptu.sba301.ats.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;
import fptu.sba301.ats.entity.Offer;
import lombok.extern.log4j.Log4j2;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Static utility class for generating Offer PDF documents using OpenPDF
 * (com.lowagie).
 *
 * pom.xml dependency required:
 * <dependency>
 * <groupId>com.github.librepdf</groupId>
 * <artifactId>openpdf</artifactId>
 * <version>1.3.35</version>
 * </dependency>
 */
@Log4j2
public final class OfferPdfGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private OfferPdfGenerator() {
    }

    public static byte[] generate(Offer offer, String candidateName, String jobTitle) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(33, 37, 41));
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(73, 80, 87));
            Font bodyFont = new Font(Font.HELVETICA, 11, Font.NORMAL, new Color(52, 58, 64));
            Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(108, 117, 125));

            // Title
            Paragraph title = new Paragraph("OFFER LETTER", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Separator
            document.add(new Chunk(Chunk.NEWLINE));

            // Candidate info
            document.add(label("Dear: ", labelFont));
            document.add(value(candidateName, bodyFont));
            document.add(label("Position: ", labelFont));
            document.add(value(jobTitle, bodyFont));
            document.add(label("Position Title: ", labelFont));
            document.add(value(safe(offer.getPositionTitle()), bodyFont));
            document.add(label("Salary: ", labelFont));
            document.add(value(offer.getSalary() != null ? "$ " + offer.getSalary().toPlainString() : "N/A", bodyFont));
            document.add(label("Start Date: ", labelFont));
            document.add(
                    value(offer.getStartDate() != null ? offer.getStartDate().format(DATE_FORMAT) : "TBD", bodyFont));
            document.add(label("Offer Expiry: ", labelFont));
            document.add(
                    value(offer.getExpiryDate() != null ? offer.getExpiryDate().format(DATE_FORMAT) : "N/A", bodyFont));

            if (offer.getNotes() != null && !offer.getNotes().isBlank()) {
                document.add(new Chunk(Chunk.NEWLINE));
                document.add(label("Additional Notes:", labelFont));
                document.add(new Paragraph(offer.getNotes(), bodyFont));
            }

            document.add(new Chunk(Chunk.NEWLINE));
            document.add(new Chunk(Chunk.NEWLINE));

            Paragraph closing = new Paragraph(
                    "We look forward to welcoming you to our team. Please sign and return by the expiry date.",
                    bodyFont);
            closing.setSpacingBefore(10);
            document.add(closing);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF for offer id={}: {}", offer.getId(), e.getMessage(), e);
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    private static Paragraph label(String text, Font font) {
        Paragraph p = new Paragraph(text, font);
        p.setSpacingBefore(8);
        return p;
    }

    private static Paragraph value(String text, Font font) {
        return new Paragraph(text, font);
    }

    private static String safe(String s) {
        return s != null ? s : "";
    }
}
