# Sub_PJT2

## 0720

1. SSAFY GIT 프로젝트관리 > 팀정보에서 개인별 Jira, Gitlab 프로젝트 생성
2. [Jira] 명세서를 보고 기능 으로부터 파생된 유저스토리(하위 서브태스크)를 백로그에 등록
   - 에픽(기능목록 또는 그룹핑해서 관리하고자 하는 큰틀)을 등록하고 스토리를 매핑
   - 이슈 난이도 및 우선순위에 따라 레벨 정하기
   - 스토리 포인트 계획
   - 작업 담당자 지정
3. 스프린트만들기
   - 백로그의 우선순위가 높은 것부터 순차적으로 해당 스프린트가 끝날 때까지할 수 있는 아이템의 수를 예측 및 개인 스스로 할 수 있는 작업의 양 결정하면서 스프린트로 드래그해서 매핑
4. **스프린트 시작** 버튼 누르고 프로젝트 시작하기
   - 스프린트 목표(Goal) 입력: 스프린트가 완료되면 릴리즈할 목표 이미지
   - Start Date: 2020/07/20 9:30 AM ~ End Date: 2020/07/24 6:00 PM
6. develop 브랜치 생성 후 기본 브랜치로 설정
7. master 브랜치 보호 설정하기
   - Maintainer 만 merge 가능 하고 아무도 push 하지 못하도록 No one 으로 설정

###  ERD 다이어그램

![KakaoTalk_20200720_134050036](C:\Users\multicampus\Desktop\2학기 ssafy\2주차\s03p12c206\README.assets\KakaoTalk_20200720_134050036.png)

> **필수**
>
>  - article: article 테이블은 기존의 게시판 역할도 하고, 나만의 페이지 안에서만 글 작성 가능하게(velog 참고)
>  - article: 글 리스트 보여줄 때, 좋아요 순으로 나열
>  - category: 나만의 페이지 안에서 내가 쓴 글의 카테고리만 선택 가능
>  - user: 사용자가 갖고 있는 내공에 따라 등급 보여주기? 아니면 등급 관련 테이블 또 만들지?(이거 아닌듯)
> 응용
>  - question: 글 리스트 보여줄 때, 걸려있는 내공 순으로 나열?
>  - 답변 채택 하나도 안하면 페널티?(이거 어려울듯)
>  - 질문자가 답변을 하나 채택하면 질문자 내공에서 그만큼 깎이는것? 아니면 그냥 일률적으로?
>
> 
>
> **테이블 목록**
>
> 1. User
>  - is_staff : (admin 페이지 따로 관리 하는 경우 필요)
>  - total_score : 유저의 내공(등급은 이 내공으로 산정)
> 이 외의 필수 속성들 (아이디, 닉네임, 패스워드 등)
>
> 2. Article
>  - user : User 외래키(일대다)
> 이 외의 필수 속성들 (제목, 내용, 생성일 등)
>
> 3. Comment
>  - user : User 외래키(일대다)
>  - article: Article 외래키(일대다)
> 이 외의 필수 속성들 (내용, 생성일 등)
>
> 4. Question
>  - user : User 외래키(일대다)
>  - score: 질문에 걸린 내공
> 이 외의 필수 속성들 (제목, 내용, 생성일 등)
>
> 5. Answer(코드블럭 관련해서 다시 논의?)
>  - user : User 외래키(일대다)
>  - question: Question 외래키(일대다)
>  - chosen: 답변 채택 여부
> 이 외의 필수 속성들 (내용, 생성일 등)
>
> 6. Category
>  - name: 카테고리 이름
>
> --------------------------------------------------------------------------
> 다대다 연결 테이블- 새로운 테이블 생성해서 외래키 두 개 받는걸로? 
> 스프링에서 다대다 테이블 어떻게 다루는지 모르겠어서 일단 이렇게 했습니다
>
> 1. User & User (follow)
>  - following : User 외래키
>
> 2. User & Article (like_articles)
>  - user: User 외래키
>  - article: Article 외래키
>
> 3. Category & Article(category_article)
>  - category : Category 외래키
>  - article: Article 외래키
>
> 4. Category & Question(category_question)
>  - question: Question 외래키
>  - category : Category 외래키
>
> ** 여기서부턴 걍 선택~(댓글 좋아요 기능이므로 좋아요 순으로 댓글 정렬한다던지 그러면 사용 가능)
>
> 5. User & Comment (like_comments)
>  - user: User 외래키
>  - comment: Comment 외래키
>
> 6. User & Answer (like_answers)
>  - user: User 외래키
>  - Answer: Answer 외래키
>
> 7. User & Question (like_question)
>  - user: User 외래키
>
>  - question: Question 외래키
>
>    

