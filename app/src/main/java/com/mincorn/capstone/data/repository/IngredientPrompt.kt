package com.mincorn.capstone.data.repository

const val CATEGORIES = "정육/계란, 채소, 과일, 수산, 간편식품, 조미료, 베이커리, 유제품, 기타"

const val PROMPT_TEXT = """
        사진 속에 있는 식재료들을 분석해서 다음 JSON 형식의 리스트로 응답해줘.
        카테고리는 반드시 다음 리스트 중 하나여야 해: [$CATEGORIES]
        
        JSON 형식 예시:
        [
          {"name": "양파", "count": 2, "category": "채소"},
          {"name": "삼겹살", "count": 1, "category": "정육/계란"}
        ]
        다른 설명 없이 오직 JSON만 응답해.
    """.trimIndent()