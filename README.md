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
- Spock Framework (테스트)

## 실행 방법

### 1. 프로젝트 빌드 및 실행

```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

### 2. 브라우저 접속

http://localhost:8080 접속

### 3. 회원가입 및 로그인

1. `/signup` 페이지에서 회원가입
2. `/login` 페이지에서 로그인
3. 로그인 성공 시 `/dashboard`로 자동 이동

## 테스트 데이터 입력 방법

INPUT_DATA1~4.json 파일을 API로 업로드하는 방법입니다.

### 1. 로그인하여 JWT 토큰 받기

```bash
# 로그인 (회원가입 먼저 완료 필요)
curl -X POST http://localhost:8080/api/members/login \
  -H "Content-Type: application/json" \
  -d '{"email": "your@email.com", "password": "yourpassword"}'
```

응답에서 `accessToken`과 `recordKey`를 확인합니다.

### 2. JSON 파일 업로드

```bash
# recordKey를 본인 계정의 것으로 변경하여 업로드
cat INPUT_DATA1.json | sed 's/"recordkey" : "[^"]*"/"recordkey": "YOUR_RECORD_KEY"/' | \
curl -X POST http://localhost:8080/api/health/data \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d @-
```

### 3. 전체 파일 일괄 업로드 스크립트

```bash
# 로그인
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/members/login \
  -H "Content-Type: application/json" \
  -d '{"email": "your@email.com", "password": "yourpassword"}')

# 토큰과 recordKey 추출
TOKEN=$(echo $LOGIN_RESPONSE | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['accessToken'])")
RECORD_KEY=$(echo $LOGIN_RESPONSE | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['member']['recordKey'])")

# 각 파일 업로드
for i in 1 2 3 4; do
  cat INPUT_DATA${i}.json | \
  python3 -c "import sys,json; d=json.load(sys.stdin); d['recordkey']='$RECORD_KEY'; print(json.dumps(d))" | \
  curl -s -X POST http://localhost:8080/api/health/data \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d @-
  echo "INPUT_DATA${i}.json 업로드 완료"
done
```

### 4. 대시보드에서 확인

- 날짜 범위: 2024-11-14 ~ 2024-12-16
- 일별 조회 또는 월별 조회로 데이터 확인

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
├── common/
│   ├── exception/              # 예외 처리 (CustomException, ErrorCode)
│   ├── response/               # 공통 응답 (ApiResponse)
│   └── util/                   # 유틸리티 (DateTimeUtil, ResponseUtil)
├── config/
│   ├── SecurityConfig.java
│   └── jwt/                    # JWT 설정
├── domain/
│   ├── member/                 # 회원 도메인
│   │   ├── controller/
│   │   ├── dto/
│   │   │   ├── request/        # 요청 DTO (SignUpRequest, LoginRequest)
│   │   │   └── response/       # 응답 DTO (MemberResponse, LoginResponse)
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   └── health/                 # 건강 데이터 도메인
│       ├── controller/
│       ├── dto/
│       │   ├── request/        # 요청 DTO (HealthDataRequest 등)
│       │   └── response/       # 응답 DTO (DailySummaryResponse 등)
│       ├── entity/
│       ├── repository/
│       └── service/
└── web/
    └── PageController.java     # 페이지 컨트롤러

src/main/resources/
├── templates/                  # Thymeleaf 템플릿
│   ├── auth/
│   │   ├── login.html
│   │   └── signup.html
│   └── dashboard/
│       └── index.html
├── static/                     # 정적 리소스
│   ├── css/
│   └── js/
└── application.yml
```

## 코딩 스타일

프로젝트 코딩 스타일 가이드는 [CODING_STYLE.md](./CODING_STYLE.md)를 참조하세요.

### 주요 규칙

- **Entity**: `*Entity` suffix 사용 (예: `MemberEntity`)
- **DTO**: request/response 패키지로 분리, 파일당 하나의 클래스
- **Service**: 메서드 분리, Javadoc 주석 필수
- **정적 팩토리 메서드**: Entity, Response DTO에서 `of()` 메서드 사용

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

## 테스트

```bash
./gradlew test
```

- Spock Framework 기반 테스트
- Service, Controller 단위 테스트 포함
