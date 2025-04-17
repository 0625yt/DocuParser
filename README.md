# DocuParser

`DocuParser`는 문서 데이터 추출 및 처리에 중점을 둔 Java 기반의 라이브러리입니다. 이 프로젝트는 다양한 문서 형식(DOCX 등)을 처리하며, 문서를 추출, 파싱, 계층화하여 JSON 형식으로 저장할 수 있도록 설계되었습니다. 코드의 유연성과 확장성을 고려하여 설계되었으며, 주요 기능은 `DataExtractContext` 및 `DocumentExtractorDocx` 클래스를 중심으로 구현되었습니다.

---

## 주요 기능

### 1. `DataExtractContext`
`DataExtractContext` 클래스는 데이터 추출 및 문서 처리 과정에서 컨텍스트 데이터를 관리하는 역할을 합니다. 
- **유연한 데이터 저장**: 키-값 기반의 Map 구조를 통해 데이터를 저장하며, 다양한 데이터 유형을 저장할 수 있습니다.
- **주요 메서드**:
  - `put(String key, Object value)`: 데이터를 추가합니다.
  - `get(String key)`: 데이터를 검색합니다.
  - `remove(String key)`: 데이터를 삭제합니다.
  - `clear()`: 모든 데이터를 삭제합니다.
  - `containsKey(String key)`: 특정 키가 존재하는지 확인합니다.
  - `size()`: 저장된 데이터의 총 개수를 반환합니다.
  - **향후 확장 가능성**: `getFile()` 메서드는 파일 관련 작업을 구현할 수 있는 여지를 남겨두었습니다.

---

### 2. `DocumentExtractorDocx`
`DocumentExtractorDocx` 클래스는 DOCX 형식의 문서를 처리하고 추출된 데이터를 JSON 파일로 저장하는 기능을 제공합니다.

- **문서 요소 추출**:
  - `extractElement(DataExtractContext context, String title, List<List<XWPFParagraph>> allFilesParagraphs, boolean multipleCheck)`: 문서의 각 단락을 읽고 처리하여 `AbstractElement` 객체로 변환합니다.
  - `createElementFromParagraph(XWPFParagraph paragraph, int sequence, String docType)`: 단락에서 요소를 생성합니다.

- **계층 구조 생성**:
  - `buildHierarchyForElements(ArrayList<AbstractElement> elementList)`: 추출된 요소를 계층 구조로 변환합니다.

- **JSON 출력**:
  - `processElementsFromContext(DataExtractContext context, String title, boolean check)`: 특정 컨텍스트 데이터를 JSON 파일로 저장합니다.

- **지원되는 문서 형식**:
  - `ElementFactory`를 통해 다양한 문서 유형(예: `Const.SABANG_NAME`, `Const.SANBANG_NAME`)을 지원합니다.

---

## 설치 및 실행

### 1. 요구 사항
- Java 8 이상
- Apache POI 라이브러리
- JSON Simple 라이브러리

### 2. 설치
1. 이 저장소를 클론합니다:
   ```bash
   git clone https://github.com/0625yt/DocuParser.git
   ```
2. 필요한 라이브러리를 설치합니다:
   ```bash
   mvn install
   ```

### 3. 실행
`DocumentExtractorDocx` 클래스를 실행하여 DOCX 문서를 처리합니다:
```java
public static void main(String[] args) {
    DataExtractContext context = new DataExtractContext();
    DocumentExtractorDocx extractor = new DocumentExtractorDocx();

    // DOCX 파일 경로 지정
    String docxFilePath = "path/to/document.docx";
    String title = "SampleTitle";
    List<List<XWPFParagraph>> paragraphs = /* 문서의 단락 데이터를 로드 */;
    boolean multipleCheck = false;

    try {
        extractor.extractElement(context, title, paragraphs, multipleCheck);
        extractor.processElementsFromContext(context, title, false);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

---

## 디렉토리 구조

```
DocuParser/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   ├── parse/
│   │   │   │   │   ├── document/
│   │   │   │   │   │   ├── DataExtractContext.java
│   │   │   │   │   │   ├── DocumentExtractorDocx.java
│   │   │   │   │   │   ├── ... (기타 문서 Handling 클래스)
│   │   │   │   │   │   ├── common/
│   │   │   │   │   │   │   ├── Const.java
│   │   │   │   │   │   │   ├── ... (기타 Util 클래스)
│   │   │   │   │   │   │   ├── enums/
│   │   │   │   │   │   │   │   ├── ContentsType.java
│   │   │   │   │   │   │   │   ├── ElementType.java
│   │   │   │   │   │   │   ├── factory/
│   │   │   │   │   │   │   │   ├── ElementFactory.java
│   │   │   │   │   │   │   │   ├── ... (기타 팩토리 클래스)
│   │   │   │   │   │   │   ├── parse/
│   │   │   │   │   │   │       ├── AbstractElement.java
│   │   │   │   │   │   │       ├── ParagraphElement.java
│   │   │   │   │   │   │   │   ├── ... (기타 Element 클래스)
└── README.md
```

---

## 개선 목표

### 1. 코드 품질 개선 목표
- **타입 안전성 강화**:
  - `DataExtractContext` 클래스에서 `Map<String, Object>` 대신 제네릭을 도입하여 데이터 유형을 명확히 해야 합니다.
- **중복 코드 제거**:
  - `DocumentExtractorDocx` 클래스의 메서드에서 중복 로직을 분리하고 재사용 가능한 메서드로 추출합니다.
- **단일 책임 원칙(SRP)**:
  - `extractElement` 메서드의 복잡도를 줄이기 위해 문서 파싱, 계층 구조 생성, JSON 출력 작업을 분리합니다.

### 2. 테스트 코드 작성
- 다양한 문서 포맷과 데이터 시나리오에 대해 단위 테스트를 작성하여 안정성을 강화합니다.

### 3. 로그 시스템 도입
- `System.out.println` 대신 로깅 프레임워크를 사용하여 디버깅과 운영 환경을 분리합니다.

---

## 기여
이 프로젝트에 기여하고 싶다면, `Issues` 또는 `Pull Requests`를 통해 의견을 공유해주세요. 기여 가이드는 곧 추가될 예정입니다.

---

## 라이선스
이 프로젝트는 [MIT 라이선스](LICENSE)를 따릅니다.
