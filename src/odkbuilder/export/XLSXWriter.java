package odkbuilder.export;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class XLSXWriter {
    private ArrayList<String> sheetNames = new ArrayList<String>();
    private ArrayList<ArrayList<String[]>> sheetRows = new ArrayList<ArrayList<String[]>>();

    public void addSheet(String name, ArrayList<String[]> rows) {
        sheetNames.add(name);
        sheetRows.add(rows);
    }

    public void write(File file) throws Exception {
        FileOutputStream fos = new FileOutputStream(file);
        ZipOutputStream zip = new ZipOutputStream(fos);

        try {
            put(zip, "[Content_Types].xml", buildContentTypes());
            put(zip, "_rels/.rels", buildRootRels());
            put(zip, "xl/workbook.xml", buildWorkbook());
            put(zip, "xl/_rels/workbook.xml.rels", buildWorkbookRels());

            for (int i = 0; i < sheetNames.size(); i++) {
                put(zip, "xl/worksheets/sheet" + (i + 1) + ".xml",
                        buildSheet(sheetRows.get(i)));
            }
        } finally {
            zip.close();
            fos.close();
        }
    }

    private void put(ZipOutputStream zip, String path, String content) throws Exception {
        zip.putNextEntry(new ZipEntry(path));
        zip.write(content.getBytes("UTF-8"));
        zip.closeEntry();
    }

    private String buildContentTypes() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        sb.append("<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">");
        sb.append("<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>");
        sb.append("<Default Extension=\"xml\" ContentType=\"application/xml\"/>");
        sb.append("<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>");
        for (int i = 0; i < sheetNames.size(); i++) {
            sb.append("<Override PartName=\"/xl/worksheets/sheet").append(i + 1)
              .append(".xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>");
        }
        sb.append("</Types>");
        return sb.toString();
    }

    private String buildRootRels() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
                + "</Relationships>";
    }

    private String buildWorkbook() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        sb.append("<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" ");
        sb.append("xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">");
        sb.append("<sheets>");
        for (int i = 0; i < sheetNames.size(); i++) {
            sb.append("<sheet name=\"").append(escape(sheetNames.get(i)))
              .append("\" sheetId=\"").append(i + 1)
              .append("\" r:id=\"rId").append(i + 1).append("\"/>");
        }
        sb.append("</sheets></workbook>");
        return sb.toString();
    }

    private String buildWorkbookRels() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        sb.append("<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">");
        for (int i = 0; i < sheetNames.size(); i++) {
            sb.append("<Relationship Id=\"rId").append(i + 1)
              .append("\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet")
              .append(i + 1).append(".xml\"/>");
        }
        sb.append("</Relationships>");
        return sb.toString();
    }

    private String buildSheet(ArrayList<String[]> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");
        sb.append("<sheetData>");

        for (int r = 0; r < rows.size(); r++) {
            String[] row = rows.get(r);
            sb.append("<row r=\"").append(r + 1).append("\">");
            for (int c = 0; c < row.length; c++) {
                String value = row[c];
                if (value == null || value.equals("")) {
                    continue;
                }
                sb.append("<c r=\"").append(columnLetter(c)).append(r + 1)
                  .append("\" t=\"inlineStr\"><is><t xml:space=\"preserve\">")
                  .append(escape(value)).append("</t></is></c>");
            }
            sb.append("</row>");
        }

        sb.append("</sheetData></worksheet>");
        return sb.toString();
    }

    private String columnLetter(int index) {
        String letters = "";
        int n = index;
        while (n >= 0) {
            letters = (char) ('A' + (n % 26)) + letters;
            n = (n / 26) - 1;
        }
        return letters;
    }

    private String escape(String s) {
        String out = s.replace("&", "&amp;");
        out = out.replace("<", "&lt;");
        out = out.replace(">", "&gt;");
        out = out.replace("\"", "&quot;");
        return out;
    }
}
