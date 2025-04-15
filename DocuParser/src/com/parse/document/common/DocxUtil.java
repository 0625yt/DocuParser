package com.parse.document.common;

import java.util.*;
import org.apache.poi.xwpf.usermodel.*;

import com.parse.document.common.parse.TableData;

public class DocxUtil {

    /**
     * DOCX 테이블을 TableData로 변환
     */
    public static TableData convertTable(XWPFTable table, int sequence, String docType) {
        List<XWPFTableRow> rows = table.getRows();

        int maxRow = rows.size();
        int maxCol = getMaxColumnCount(rows);

        String[][] arrayData = new String[maxRow][maxCol];
        String[][] mergeInfo = new String[maxRow][maxCol];

        TableData tableData = new TableData(maxRow, maxCol, sequence, "");

        for (int r = 0; r < rows.size(); r++) {
            XWPFTableRow row = rows.get(r);
            List<XWPFTableCell> cells = row.getTableCells();

            int colCursor = 0;
            for (XWPFTableCell cell : cells) {
                while (colCursor < maxCol && arrayData[r][colCursor] != null) {
                    colCursor++; // 병합 셀로 인해 비어있는 자리를 찾아감
                }

                String text = cell.getText().trim();
                int rowSpan = getRowSpan(cell);
                int colSpan = getColSpan(cell);

                rowSpan = Math.max(rowSpan, 1);
                colSpan = Math.max(colSpan, 1);

                for (int i = 0; i < rowSpan; i++) {
                    for (int j = 0; j < colSpan; j++) {
                        if ((r + i) < maxRow && (colCursor + j) < maxCol) {
                            arrayData[r + i][colCursor + j] = text;
                            mergeInfo[r + i][colCursor + j] = rowSpan + "|" + colSpan;

                            tableData.setData(r + i, colCursor + j, text);
                            tableData.setMergeInfo(r + i, colCursor + j, rowSpan + "|" + colSpan);
                        }
                    }
                }

                colCursor += colSpan;
            }
        }

        return tableData;
    }

    /**
     * 최대 열 수를 계산
     */
    private static int getMaxColumnCount(List<XWPFTableRow> rows) {
        int max = 0;
        for (XWPFTableRow row : rows) {
            max = Math.max(max, row.getTableCells().size());
        }
        return max;
    }

    /**
     * colSpan (gridSpan 속성) 추출
     */
    private static int getColSpan(XWPFTableCell cell) {
        try {
            return cell.getCTTc().getTcPr().getGridSpan().getVal().intValue();
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * rowSpan 은 Word에서 w:vMerge 로 표현됨
     * 단, Word는 첫 셀에만 명시되므로 계산은 수작업이 필요할 수 있음
     */
    private static int getRowSpan(XWPFTableCell cell) {
        // Apache POI는 rowSpan 정보 직접 제공하지 않음
        // 필요 시, Word XML 분석해서 구현
        return 1;
    }
}
