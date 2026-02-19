package com.nanba.hussain.invoice;

import com.nanba.hussain.order.FoodOrder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoicePdfService {

    private static final DateTimeFormatter INVOICE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public byte[] generateInvoice(FoodOrder order) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                drawHeader(content, page);
                drawInvoiceMeta(content, order);
                drawItems(content, order);
                drawTotal(content, order);
                drawFooter(content, page);
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to generate invoice PDF", exception);
        }
    }

    private void drawHeader(PDPageContentStream content, PDPage page) throws IOException {
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();

        content.setNonStrokingColor(new Color(173, 40, 29));
        content.addRect(0, pageHeight - 100, pageWidth, 100);
        content.fill();

        writeText(content, "Foodie Online Orders", 40, pageHeight - 48, 20, true, Color.WHITE);
        writeText(content, "INVOICE", pageWidth - 155, pageHeight - 52, 22, true, Color.WHITE);
        writeText(content, "123 Food Street, Chennai | +91 98765 43210", 40, pageHeight - 74, 10, false, Color.WHITE);
    }

    private void drawInvoiceMeta(PDPageContentStream content, FoodOrder order) throws IOException {
        writeText(content, "Invoice Details", 40, 700, 14, true, new Color(33, 37, 41));

        writeText(content, "Order ID:", 40, 675, 11, true, Color.DARK_GRAY);
        writeText(content, String.valueOf(order.getId()), 140, 675, 11, false, Color.BLACK);

        writeText(content, "Invoice Date:", 40, 655, 11, true, Color.DARK_GRAY);
        writeText(content, order.getOrderedAt().format(INVOICE_DATE_FORMAT), 140, 655, 11, false, Color.BLACK);

        writeText(content, "Total Items:", 40, 635, 11, true, Color.DARK_GRAY);
        writeText(content, String.valueOf(order.getItemCount()), 140, 635, 11, false, Color.BLACK);

        content.setStrokingColor(new Color(230, 230, 230));
        content.moveTo(40, 620);
        content.lineTo(555, 620);
        content.stroke();
    }

    private void drawItems(PDPageContentStream content, FoodOrder order) throws IOException {
        writeText(content, "Ordered Items", 40, 595, 13, true, new Color(33, 37, 41));

        float y = 572;
        int index = 1;
        for (String line : splitOrderLines(order.getItemsSummary())) {
            List<String> wrapped = wrap(line, 72);
            boolean first = true;
            for (String wrappedLine : wrapped) {
                String prefix = first ? index + ". " : "   ";
                writeText(content, prefix + wrappedLine, 50, y, 11, false, new Color(55, 65, 81));
                y -= 16;
                first = false;
            }
            index++;
            y -= 3;
            if (y < 130) {
                break;
            }
        }
    }

    private void drawTotal(PDPageContentStream content, FoodOrder order) throws IOException {
        content.setNonStrokingColor(new Color(255, 243, 236));
        content.addRect(40, 110, 515, 42);
        content.fill();

        content.setStrokingColor(new Color(240, 187, 158));
        content.addRect(40, 110, 515, 42);
        content.stroke();

        writeText(content, "Total Amount", 50, 127, 13, true, new Color(127, 29, 29));
        writeText(content, "Rs. " + order.getTotalAmount(), 470, 127, 14, true, new Color(127, 29, 29));
    }

    private void drawFooter(PDPageContentStream content, PDPage page) throws IOException {
        float footerY = 62;
        content.setStrokingColor(new Color(220, 220, 220));
        content.moveTo(40, footerY + 13);
        content.lineTo(page.getMediaBox().getWidth() - 40, footerY + 13);
        content.stroke();

        writeText(content, "Thank you for your order.", 40, footerY, 10, false, new Color(90, 90, 90));
        writeText(content, "This is a computer-generated invoice.", 390, footerY, 10, false, new Color(90, 90, 90));
    }

    private void writeText(
            PDPageContentStream content,
            String text,
            float x,
            float y,
            int fontSize,
            boolean bold,
            Color color) throws IOException {
        PDType1Font font = bold
                ? new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
                : new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        content.beginText();
        content.setFont(font, fontSize);
        content.setNonStrokingColor(color);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    private List<String> splitOrderLines(String itemsSummary) {
        List<String> lines = new ArrayList<>();
        if (itemsSummary == null || itemsSummary.isBlank()) {
            lines.add("No items");
            return lines;
        }

        for (String piece : itemsSummary.split(",")) {
            String item = piece.trim();
            if (!item.isEmpty()) {
                lines.add(item);
            }
        }

        if (lines.isEmpty()) {
            lines.add("No items");
        }
        return lines;
    }

    private List<String> wrap(String text, int maxLength) {
        List<String> lines = new ArrayList<>();
        if (text.length() <= maxLength) {
            lines.add(text);
            return lines;
        }

        String[] words = text.split("\\s+");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            if (current.length() + word.length() + 1 > maxLength) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                if (current.length() > 0) {
                    current.append(' ');
                }
                current.append(word);
            }
        }

        if (current.length() > 0) {
            lines.add(current.toString());
        }
        return lines;
    }
}
