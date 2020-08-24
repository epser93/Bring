# WebBlog : RELEASE Version 1.00

## 배포

- http://lawliet0521.com
- API Docs(Swagger): http://lawliet0521.com/api/swagger-ui.html

## 주요 기술스택

- CI/CD: Nginx, GitLab CI/CD(Jenkins initially)
- Server: GCP VM(Ubuntu 18.04), Docker
- Storage: AWS S3(CloudFront)
- DB: Mariadb(Docker), Reids(Docker), JPA
- Back-end Framework: Spring Boot
- Front-end: Vue js, axios
- Library: Spring Security, OAuth2.0(JWT)

## 주요 기능

- 회원가입 & 로그인
- 프로필 기능 : 회원 정보 수정 및 프로필 사진 등록, 팔로워 및 팔로잉 목록, 데일리 포스트 수 기반 잔디 Api, 일일 방문자 그래프, 태그 사용 목록 및 그래프
- 개인 블로그 기능 : 게시판 CRUD, 개인 포스트 CRUD, Q&A CRUD 및 답변 기록 공유
- 메인페이지: 블로그 포스트 최신글(14일 이내), 블로그 포스트 트렌드(인기글, 조회수 40이상 or 추천수 20개 이상, 14일 이내), Q&A 최신글(14일 이내), Q&A 트렌드(인기글, 조회수 40 이상 or 답변 수 1 이상,14일 이내), 태그 검색 기능 및 인기 태그 Top 30
- 헤더: 메뉴바, 검색창

##  ERD 다이어그램

![ERD_Diagram](https://dp02rmdt3p3bw.cloudfront.net/ERD_Diagram.PNG)
