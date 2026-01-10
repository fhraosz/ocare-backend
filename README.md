# 오케어 백엔드 과제

건강 활동 데이터(삼성헬스/애플건강) 수집 및 저장 서비스 백엔드 API

## 기술 스택

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security + JWT
- H2 Database (Embedded)
- Embedded Redis

## 프로젝트 구조

```
src/main/java/com/ocare/
├── OcareApplication.java           # 메인 애플리케이션
├── config/                         # 설정 클래스
│   ├── SecurityConfig.java         # Spring Security 설정
│   ├── JpaConfig.java              # JPA Auditing 설정
│   ├── EmbeddedRedisConfig.java    # Embedded Redis 설정
│   └── jwt/                        # JWT 관련 클래스
├── domain/
│   ├── member/                     # 회원 도메인
│   │   ├── entity/Member.java
│   │   ├── repository/
│   │   ├── service/
│   │   ├── controller/
│   │   └── dto/
│   └── health/                     # 건강 데이터 도메인
│       ├── entity/
│       │   ├── HealthEntry.java          # 10분 단위 원본 데이터
│       │   ├── DailyHealthSummary.java   # 일별 집계
│       │   └── MonthlyHealthSummary.java # 월별 집계
│       ├── repository/
│       ├── service/
│       ├── controller/
│       └── dto/
└── common/                         # 공통 클래스
    ├── exception/
    └── response/
```

## 실행 방법

```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

## API 명세

### 회원 API

| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/members/signup | 회원가입 |
| POST | /api/members/login | 로그인 |

### 건강 데이터 API

| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/health/data | 건강 데이터 저장 |
| GET | /api/health/daily | 일별 집계 조회 |
| GET | /api/health/daily/{date} | 특정 일자 집계 조회 |
| GET | /api/health/monthly | 월별 집계 조회 |
| GET | /api/health/monthly/{year}/{month} | 특정 월 집계 조회 |

## 데이터베이스 설계

### members (회원)
- id, name, nickname, email, password, record_key, created_at, updated_at

### health_entries (건강 데이터 원본 - 10분 단위)
- id, record_key, period_from, period_to, steps, calories, distance, created_at

### daily_health_summary (일별 집계)
- id, record_key, summary_date, total_steps, total_calories, total_distance

### monthly_health_summary (월별 집계)
- id, record_key, summary_year, summary_month, total_steps, total_calories, total_distance

## H2 Console

- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:file:./data/ocaredb
- Username: sa
- Password: (empty)
