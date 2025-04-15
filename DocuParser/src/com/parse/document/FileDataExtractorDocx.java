package com.parse.document;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.IBodyElement;

import com.parse.document.common.Util;

public abstract class FileDataExtractorDocx extends FileDataExtractor {

	private static final String CATEGORY = "약관목차";
	private static final String FIND_FIRST_GAWN = "제1관목적및용어의정의";
	private static final String WORD = "WORD";
	
	public XWPFDocument[] getDocxFiles(DataExtractContext context, String title) {
		String key = "DocxFileData.Docx." + title;
		Object object = context.get(key);
		if (object != null && object instanceof XWPFDocument[]) {
			return (XWPFDocument[]) object;
		}

		List<XWPFDocument> docxList = new ArrayList<>();
		try {
			File[] files = downloads(context, title, WORD);
			if (files != null) {
				for (File file : files) {
					try (FileInputStream fis = new FileInputStream(file)) {
						XWPFDocument doc = new XWPFDocument(fis);
						if (doc != null) {
							docxList.add(doc);
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.println(MessageFormat.format("\"{0}\" DOCX 문서 파싱 실패: {1}", title, e.getMessage()));
		}

		XWPFDocument[] documents = docxList.toArray(new XWPFDocument[0]);
		context.put(key, documents);
		return documents;
	}

	public void processDocument(DataExtractContext context, String title,
			DocumentExtractorDocx documentExtractor, boolean toJson) throws Exception {

		XWPFDocument[] docxFiles = getDocxFiles(context, title);

		if (docxFiles != null && docxFiles.length > 0) {
			List<XWPFDocument> docList = new ArrayList<>();
			docList.add(docxFiles[0]);

			DataExtractContext fileContext = new DataExtractContext();
			List<List<XWPFParagraph>> allFilesParagraphs = new ArrayList<>();

			List<XWPFParagraph> paragraphs = new ArrayList<>();
			for (IBodyElement bodyElement : docxFiles[0].getBodyElements()) {
				if (bodyElement instanceof XWPFParagraph) {
					paragraphs.add((XWPFParagraph) bodyElement);
				}
			}
			allFilesParagraphs.add(paragraphs);

			documentExtractor.extractElement(fileContext, title, allFilesParagraphs, false);

			if (toJson) {
				documentExtractor.processElementsFromContext(fileContext, title, false);
			} else {
				documentExtractor.printElement(fileContext, title);
			}
		}
	}

	public void processDocumentEx(DataExtractContext context, String title,
			DocumentExtractorDocx documentExtractor, String bojongName, boolean toJson) throws Exception {

		long startTime = System.currentTimeMillis();
		XWPFDocument[] docxFiles = getDocxFiles(context, title);
		boolean multipleCheck = yackgawnCheck(docxFiles, title);
		boolean input = false;

		if (multipleCheck && docxFiles != null) {
			List<List<XWPFParagraph>> allFilesParagraphs = new ArrayList<>();

			for (XWPFDocument docx : docxFiles) {
				List<XWPFParagraph> paragraphs = new ArrayList<>();
				for (IBodyElement element : docx.getBodyElements()) {
					if (element instanceof XWPFParagraph) {
						paragraphs.add((XWPFParagraph) element);
					}
				}

				List<XWPFParagraph> selectedParagraphs = new ArrayList<>();

				for (int i = 0; i < paragraphs.size(); i++) {
					String text = Util.replaceBlank(paragraphs.get(i).getText());
					if (text.equals(FIND_FIRST_GAWN)) {
						if (i >= 3) {
							String preText = Util.replaceBlank(paragraphs.get(i - 3).getText());
							if (preText.isEmpty()) {
								preText = Util.replaceBlank(paragraphs.get(i - 2).getText());
							}
							String prevText = Util.replaceBlank(paragraphs.get(i - 1).getText());
							if (!CATEGORY.equals(prevText) && bojongName.contains(Util.returnName(preText))) {
								input = true;
							}
						}
					} else if (input && text.equals(CATEGORY)) {
						input = false;
					}

					if (input) {
						selectedParagraphs.add(paragraphs.get(i));
					}
				}

				allFilesParagraphs.add(selectedParagraphs);
			}

			documentExtractor.extractElement(context, title + bojongName, allFilesParagraphs, true);
		} else {
			processDocument(context, title, documentExtractor, toJson);
		}
	}

	/**
	 * 약관목차가 2번 이상 나오면 다중 약관으로 판단
	 */
	private boolean yackgawnCheck(XWPFDocument[] docxFiles, String title) {
		int count = 0;
		if (docxFiles != null) {
			for (XWPFDocument docx : docxFiles) {
				for (IBodyElement element : docx.getBodyElements()) {
					if (element instanceof XWPFParagraph) {
						String text = Util.replaceBlank(((XWPFParagraph) element).getText());
						if (text.equals(CATEGORY)) {
							count++;
							if (count >= 2) return true;
						}
					}
				}
			}
		}
		return false;
	}
}
