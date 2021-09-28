# 와플스튜디오 SpringBoot Seminar[2] 과제 마무리

### due: 2021.09.28. 23:59
## finished: 2021.09.25. 13:20


### 배운 점
1. OneToMany, ManyToOne는 말 그대로 참고를 붙여준 것이다. @OneToOne, mappedBy로 list를 join 시켰으면 그 list를 직접 조작해주어야 반영이 된다. 즉 연관 관계를 갖는 상대 테이블로부터 구조만 따오는 것이고, 용도는 사용자의 몫에 달렸다.
2. Repository의 함수(JpaRepository 내장 함수(save), 사용자 정의 함수 등) + 사용자가 정의한 Dto를 이용하여 Service를 개발하면 원하는 API를 만들 수 있다.
3. database에 저장하는 정보들을 필요에 따라 적절히 배합하여 원하는 데이터 구조(Dto)를 만들 수 있다. 이때 서로 연관 관계를 갖는 테이블의 데이터들은 참조가 매우 용이해진다.
4. authorization 에 Bearar 토큰을 넣어 Request header와 함께 보낼 수도 있고, 회원가입/로그인 후에 Response header에 받아올 수도 있다.
5. .? / .!! 문법을 이해하였다.
6. @JsonIgnore을 알게 되었다. 연관 관계를 설정해주어야 하지만 model을 정의할 때 굳이 parameter로 넣어주어야 할 필요가 없거나, Dto를 구성할 때 출력되지 않았으면 하는 column을 무시할 수 있다.

### 아쉬운 점
1. 배운 점 1.을 늦게 깨달았던 점이 아쉽다. 새로운 지식을 배울 때는 고정관념을 적절히 활용할 줄 알아야 한다는 것을 느꼈다.

### 궁금한 점
