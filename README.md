# O-Care Backend

건강 활동 데이터(삼성헬스/애플건강) 수집 및 저장 서비스

## 기술 스택

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security + JWT
- Thymeleaf + Bootstrap 5
- H2 Database (Embedded)
- Embedded Redis

## 실행 방법

```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

실행 후 http://localhost:8080 접속

## 페이지 구성

| 페이지 | URL | 설명 |
|--------|-----|------|
| 로그인 | /login | 이메일/비밀번호 로그인 |
| 회원가입 | /signup | 회원 등록 |
| 대시보드 | /dashboard | 건강 데이터 조회 (일별/월별) |

## API 명세

### 회원 API

| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/members/signup | 회원가입 |
| POST | /api/members/login | 로그인 (JWT 토큰 발급) |

### 건강 데이터 API

| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/health/data | 건강 데이터 저장 |
| GET | /api/health/daily | 일별 집계 조회 |
| GET | /api/health/daily/{date} | 특정 일자 집계 조회 |
| GET | /api/health/monthly | 월별 집계 조회 |
| GET | /api/health/monthly/{year}/{month} | 특정 월 집계 조회 |

## 프로젝트 구조

```
src/main/java/com/ocare/
├── config/                    # 설정 클래스
│   ├── SecurityConfig.java
│   └── jwt/
├── domain/
│   ├── member/                # 회원 도메인
│   └── health/                # 건강 데이터 도메인
├── web/
│   └── PageController.java    # 페이지 컨트롤러
└── common/                    # 공통 클래스

src/main/resources/
├── templates/                 # Thymeleaf 템플릿
│   ├── auth/
│   │   ├── login.html
│   │   └── signup.html
│   └── dashboard/
│       └── index.html
├── static/                    # 정적 리소스
│   ├── css/
│   └── js/
└── application.yml
```

## 데이터베이스

### 테이블 구조

| 테이블 | 설명 |
|--------|------|
| members | 회원 정보 |
| health_entries | 건강 데이터 원본 (10분 단위) |
| daily_health_summary | 일별 집계 |
| monthly_health_summary | 월별 집계 |

### H2 Console

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/ocaredb`
- Username: `sa`
- Password: (없음)

## 테스트 데이터

INPUT_DATA1~4.json 파일의 데이터 범위:
- 시작일: 2024-11-14
- 종료일: 2024-12-16

## 테스트

```bash
./gradlew test
```

- 총 58개 테스트 케이스 (Groovy/Spock)
