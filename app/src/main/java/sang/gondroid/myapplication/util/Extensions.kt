package sang.gondroid.myapplication.util

import sang.gondroid.myapplication.domain.model.BaseModel

/**
 * 1. 일반적인 Generic Function Body에서 타입 T는 런타임에는 Type erasure 때문에 접근이 불가능
 * 하지만 reified 타입 파라미터와 함께 inline 함수를 만들면, 런타임에 타입 T에 접근이 가능하며,
 * "변수 is T"를 통해 "변수"가 T의 인스턴스인지 검사할 수 있음
 *
 * 2. Type erasure : Generic은 컴파일 시간에 엄격한 Type 검사를 제공하지만,
 * Generic을 구현하기 위해 Java 컴파일러는 Type erasure를 적용합니다
 */
inline fun <reified T> List<BaseModel>.checkType() =
    all { it is T }
