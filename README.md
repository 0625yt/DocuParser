# DocuParser - 문서 파싱 및 분석 도구

DocuParser은 다양한 문서 포맷(DOCX, HWP 등)을 파싱하고 데이터를 추출하여 JSON과 같은 구조화된 형식으로 변환하는 강력한 도구입니다. 또한 분석 및 시각화를 통해 데이터를 쉽게 이해할 수 있도록 돕습니다.

## 주요 기능

- **문서 파싱**
  - DOCX 및 HWP 파일의 텍스트와 테이블 데이터를 추출.
  - 병합된 셀과 다중 포맷 문서 처리 가능.

- **데이터 변환**
  - 추출된 데이터를 JSON, CSV 또는 XML 포맷으로 변환.
  - 퍼센트, 수식 및 특정 패턴 처리 유틸리티 제공.

- **확장성**
  - 모듈식 설계로 새로운 문서 포맷 지원 가능.

## 설치 방법

1. 저장소를 클론합니다:
   ```bash
   git clone https://github.com/0625yt/DocuParser.git
   cd DocuParser
   ```

2. Maven(또는 선호하는 빌드 도구)을 사용하여 프로젝트를 빌드합니다:
   ```bash
   mvn clean install
   ```

3. 애플리케이션을 실행합니다:
   ```bash
   java -jar target/DocuParser.jar
   ```

## 사용 방법

### 문서 파싱
CLI(명령줄 인터페이스)를 사용하거나 해당 라이브러리를 프로젝트에 통합하여 문서를 파싱할 수 있습니다.

#### DOCX 파일 파싱 예제
```java
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
```

#### HWP 파일 파싱 예제
```java
	private DocumentExtractorHwp documentExtractorHwp = new DocumentExtractorHwp();
	private boolean toJson = false;

	public static void main(String[] args) {
		DataExtractContext context = new DataExtractContext();

		ParsingOutputHwp testInstance = new ParsingOutputHwp();
		testInstance.makeValue(context, testInstance.toJson);
	}

	public void makeValue(DataExtractContext context, boolean toJson) {
		getHWPFile( context, Const.YACK_GUAN_NAME);
		try {
			processDocument(context, Const.YACK_GUAN_NAME, documentExtractorHwp, toJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
```

### 데이터 변환
DocuParser은 추출된 데이터를 JSON, CSV 또는 XML로 변환할 수 있습니다.

## 디렉토리 구조
```
DocuParser/
├── src/
│   ├── parsers/               # DOCX, HWP 등 다양한 문서 포맷의 파싱 로직
│   │   ├── DocxParser.java
│   │   ├── HwpParser.java
│   │   └── AbstractParser.java
│   ├── utils/                 # 정규식, 파일 I/O, 데이터 변환 유틸리티
│   ├── services/              # 데이터 처리 및 시각화
│   └── models/                # 데이터 모델 정의
└── resources/
    └── application.properties # 설정 파일
```

## 기여 방법

1. 저장소를 포크합니다.
2. 새로운 브랜치를 생성합니다:
   ```bash
   git checkout -b feature-branch-name
   ```
3. 코드를 커밋합니다:
   ```bash
   git commit -m "Add new feature"
   ```
4. 브랜치를 푸시합니다:
   ```bash
   git push origin feature-branch-name
   ```
5. Pull Request를 생성합니다.

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 [LICENSE](./LICENSE)를 참고하세요.
