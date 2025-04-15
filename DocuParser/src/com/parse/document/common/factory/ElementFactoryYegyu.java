package com.parse.document.common.factory;

import java.util.regex.Pattern;

import com.parse.document.common.Const;
import com.parse.document.common.enums.ContentsType;
import com.parse.document.common.parse.ParagraphElement;

/**
 * ElementFactory 클래스는 다양한 요소(Element)를 생성하는 역할을 합니다. 이 클래스는 주어진 텍스트를 분석하여 해당
 * 텍스트의 유형에 맞는 요소를 생성합니다.
 * 
 * @author YTHONG
 */
public class ElementFactoryYegyu extends ElementFactory {
	/**
	 * 예규 텍스트를 분석하여 요소의 유형을 설정합니다.
	 * 
	 * @param text 분석할 텍스트
	 * @param elem 분석 결과를 저장할 ParagraphElement 객체
	 */
	@Override
	protected void parseTextAndSetElementType(String text, ParagraphElement elem) {
		checkJuseokStart(text);

		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);

			if (Character.isDigit(ch)) {
				handleDigitCharacter(text, elem, i); // 숫자 문자를 처리
				break;
			}else if (Pattern.matches(Const.BUPYOPATTERN, text)) {
				setType(elem, ContentsType.ATTACHED); // 부표 패턴에 해당하는 경우 ATTACHED로 설정
				break;
			} else if (isInCharSet(ch, SpecialCharSet.HANG)) {
				setType(elem, ContentsType.HANG); // 항 문자 처리
				break;
			} else if (isInCharSet(ch, SpecialCharSet.MOK)) {
				handleMokCharacter(text, elem); // 목 문자 처리
				break;
			} else if (isInCharSet(ch, SpecialCharSet.RANDOM)) {
				setType(elem, ContentsType.JO); // 랜덤 문자 처리
				break;
			} else if (text.startsWith("부표", i)) {
				setType(elem, ContentsType.ATTACHED); // 부표로 시작하는 경우 처리
				break;
			}
		}
	}	
	@Override
	protected void setType(ParagraphElement elem, ContentsType type) {
		elem.setContentsType(type);
	}
}
