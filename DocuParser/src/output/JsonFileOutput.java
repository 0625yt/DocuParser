package output;

import com.parse.document.DataExtractContext;
import com.parse.document.common.Const;
import com.parse.document.DocumentExtractorHwp;
import com.parse.document.FileDataExtractorHwp;

public class JsonFileOutput extends FileDataExtractorHwp {

	private DocumentExtractorHwp documentExtractorHwp = new DocumentExtractorHwp();
	private boolean toJson = true;
	
	public static void main(String[] args) {
		DataExtractContext context = new DataExtractContext();

		JsonFileOutput testInstance = new JsonFileOutput();
		testInstance.makeValue(context, testInstance.toJson);
	}

	public void makeValue(DataExtractContext context,boolean toJson) {
		getHWPFile(context, Const.YACK_GUAN_NAME);
		try {
			processDocument(context, Const.YACK_GUAN_NAME, documentExtractorHwp, toJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
