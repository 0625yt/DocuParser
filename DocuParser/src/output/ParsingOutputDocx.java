package output;

import com.parse.document.DataExtractContext;
import com.parse.document.common.Const;
import com.parse.document.DocumentExtractorDocx;
import com.parse.document.FileDataExtractorDocx;

public class ParsingOutputDocx extends FileDataExtractorDocx {

	private DocumentExtractorDocx documentExtractorDocx = new DocumentExtractorDocx();
	private boolean toJson = false;

	public static void main(String[] args) {
		DataExtractContext context = new DataExtractContext();

		ParsingOutputDocx testInstance = new ParsingOutputDocx();
		testInstance.makeValue(context, testInstance.toJson);
	}

	public void makeValue(DataExtractContext context, boolean toJson) {
		getDocxFiles(context, Const.YACK_GUAN_NAME);
		try {
			processDocument(context, Const.YACK_GUAN_NAME, documentExtractorDocx, toJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
