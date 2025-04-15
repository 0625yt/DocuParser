package com.parse.document;

import com.parse.document.common.Const;
import com.parse.document.common.enums.ContentsType;
import com.parse.document.common.enums.ElementType;
import com.parse.document.common.factory.*;
import com.parse.document.common.parse.*;

import org.apache.poi.xwpf.usermodel.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

public class DocumentExtractorDocx {

    private ElementFactory elementFactory;

    protected ElementFactory getElementFactory(String doctype) {
        if (doctype.equals(Const.SABANG_NAME)) {
            elementFactory = new ElementFactorySabang();
        } else if (doctype.equals(Const.SANBANG_NAME)) {
            elementFactory = new ElementFactorySanbang();
        } else if (doctype.equals(Const.YE_GYU_NAME)) {
            elementFactory = new ElementFactoryYegyu();
        } else {
            elementFactory = new ElementFactory();
        }
        return elementFactory;
    }

    public List<AbstractElement[]> extractElement(DataExtractContext context, String title,
                                                  List<List<XWPFParagraph>> allFilesParagraphs, boolean multipleCheck) throws Exception {
        ArrayList<AbstractElement[]> fileElementArrays = new ArrayList<>();

        for (List<XWPFParagraph> paragraphs : allFilesParagraphs) {
            ArrayList<AbstractElement> elementList = new ArrayList<>();
            int elementSequence = 1;

            for (XWPFParagraph paragraph : paragraphs) {
                List<AbstractElement> elements = createElementFromParagraph(paragraph, elementSequence, title);
                elementList.addAll(elements);
                elementSequence += elements.size();
            }

            buildHierarchyForElements(elementList);
            fileElementArrays.add(elementList.toArray(new AbstractElement[0]));
        }

        context.put("Elements.Docx." + title, fileElementArrays);

        if (!multipleCheck) {
            makeOutlines(context, title, fileElementArrays);
        } else {
            makeMultipleOutlines(context, title, fileElementArrays);
        }

        return fileElementArrays;
    }

    private List<AbstractElement> createElementFromParagraph(XWPFParagraph paragraph, int sequence, String docType) {
        List<AbstractElement> elements = new ArrayList<>();
        String text = paragraph.getText();
        if (text != null && !text.trim().isEmpty()) {
            AbstractElement paraElement = getElementFactory(docType).createParagraphElement(text.trim(), sequence);
            System.out.println(paraElement.getText() + " || " + paraElement.getContentsType());
            elements.add(paraElement);
        }
        return elements;
    }

    private void buildHierarchyForElements(ArrayList<AbstractElement> elementList) {
        if (elementList == null || elementList.isEmpty()) return;
        ParagraphElement root = new ParagraphElement(ElementType.TEXT);
        getElementFactory("").buildHierarchy(root, elementList);
        elementList.clear();
        elementList.addAll(root.getChildren());
    }

    public void processElementsFromContext(DataExtractContext context, String title, boolean check) {
        List<AbstractElement[]> elementArrays = (List<AbstractElement[]>) context.get("Elements.Docx." + title);
        if (elementArrays == null || elementArrays.isEmpty()) return;

        int index = 1;
        for (AbstractElement[] elements : elementArrays) {
            JSONArray array = new JSONArray();
            for (AbstractElement ele : elements) {
                array.add(convertElementToJson(ele));
            }

            String filename = generateUniqueFileName("output_" + title + "_elements" + index, "json");
            saveJsonToFile(array, Const.JSON_UPLOAD_PATH + filename);
            index++;
        }
    }

    public void printElement(DataExtractContext context, String title) {
        List<AbstractElement[]> elementArrays = (List<AbstractElement[]>) context.get("Elements.Docx." + title);
        if (elementArrays == null || elementArrays.isEmpty()) return;

        for (AbstractElement[] elements : elementArrays) {
            for (AbstractElement element : elements) {
                printElementRecursive(element, 0);
            }
        }
    }

    private void printElementRecursive(AbstractElement element, int depth) {
        if (element == null) return;
        String indent = "  ".repeat(depth);
        System.out.println(indent + "Text: " + element.getText());
        for (AbstractElement child : element.getChildren()) {
            printElementRecursive(child, depth + 1);
        }
    }

    public JSONObject convertElementToJson(AbstractElement element) {
        JSONObject obj = new JSONObject();
        obj.put("no", element.getNo());
        obj.put("contentsType", element.getContentsType() != null ? element.getContentsType().toString() : null);
        obj.put("elementType", element.getElementType() != null ? element.getElementType().toString() : null);
        obj.put("text", element.getText());
        obj.put("children", getChildrenAsJsonArray(element));
        return obj;
    }

    private JSONArray getChildrenAsJsonArray(AbstractElement element) {
        JSONArray children = new JSONArray();
        for (AbstractElement child : element.getChildren()) {
            children.add(convertElementToJson(child));
        }
        return children;
    }

    private String generateUniqueFileName(String baseName, String extension) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String uuid = UUID.randomUUID().toString();
        return String.format("%s_%s_%s.%s", baseName, timestamp, uuid, extension);
    }

    public void saveJsonToFile(JSONArray jsonArray, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(jsonArray.toJSONString());
            System.out.println("JSON data has been saved to: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save JSON to file", e);
        }
    }

    public void makeOutlines(DataExtractContext context, String title, ArrayList<AbstractElement[]> elementsList) {
        ArrayList<Outline[]> fileOutlineArrays = new ArrayList<>();
        for (AbstractElement[] elements : elementsList) {
            Outline[] outlineArray = Arrays.stream(elements)
                    .filter(Objects::nonNull)
                    .filter(ele -> isOutlineType(ele.getContentsType()))
                    .map(Outline::new)
                    .toArray(Outline[]::new);
            fileOutlineArrays.add(outlineArray);
        }
        context.put("Outlines.Docx." + title, fileOutlineArrays);
    }

    public void makeMultipleOutlines(DataExtractContext context, String title, ArrayList<AbstractElement[]> elementsList) {
        makeOutlines(context, title, elementsList);
    }

    private boolean isOutlineType(ContentsType type) {
        return type == ContentsType.GWAN || type == ContentsType.JO || type == ContentsType.HANG
                || type == ContentsType.HO || type == ContentsType.MOK || type == ContentsType.ATTACHED;
    }
}