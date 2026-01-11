# O'Care Backend Coding Style Guide

## 1. 프로젝트 구조

```
src/main/java/com/ocare/
├── common/
│   ├── exception/          # 공통 예외 처리
│   ├── response/           # 공통 응답 객체
│   └── util/               # 유틸리티 클래스
├── config/                 # 설정 클래스
└── domain/
    ├── member/             # 회원 도메인
    │   ├── controller/
    │   ├── dto/
    │   │   ├── request/    # 요청 DTO
    │   │   └── response/   # 응답 DTO
    │   ├── entity/
    │   ├── repository/
    │   └── service/
    └── health/             # 건강 데이터 도메인
        ├── controller/
        ├── dto/
        │   ├── request/
        │   └── response/
        ├── entity/
        ├── repository/
        └── service/
```

## 2. 네이밍 컨벤션

### 2.1 클래스 네이밍

| 유형 | 네이밍 규칙 | 예시 |
|------|------------|------|
| Entity | `*Entity` | `MemberEntity`, `HealthEntryEntity` |
| DTO (요청) | `*Request` | `SignUpRequest`, `LoginRequest` |
| DTO (응답) | `*Response` | `MemberResponse`, `LoginResponse` |
| Service | `*Service` | `MemberService`, `HealthDataService` |
| Controller | `*Controller` | `MemberController`, `HealthController` |
| Repository | `*Repository` | `MemberRepository`, `HealthEntryRepository` |

### 2.2 메서드 네이밍

```java
// 조회: get*, find*
public MemberEntity findByEmail(String email)
public List<DailySummaryResponse> getDailySummaries(String recordKey)

// 생성: create*, save*
public MemberResponse signUp(SignUpRequest request)
public HealthDataSaveResponse saveHealthData(HealthDataRequest request)

// 수정: update*
public void updateSummary(DailyAggregation agg)

// 삭제: delete*, remove*
public void deleteMember(Long id)

// 검증: validate*
private void validateDuplicateEmail(String email)
private void validatePassword(String rawPassword, String encodedPassword)
```

## 3. DTO 규칙

### 3.1 파일 분리
- 하나의 파일에 하나의 클래스만 정의
- 내부 클래스 사용 금지 (별도 파일로 분리)

### 3.2 필수 어노테이션
```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    private String name;
    private String email;
    // ...
}
```

### 3.3 응답 DTO 정적 팩토리 메서드
```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {
    private Long id;
    private String name;
    // ...

    public static MemberResponse of(MemberEntity entity) {
        return MemberResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
```

## 4. Entity 규칙

### 4.1 필수 어노테이션
```java
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "MEMBER")
public class MemberEntity {
    // ...
}
```

### 4.2 정적 팩토리 메서드
```java
public static MemberEntity of(String name, String nickname, String email, String password) {
    LocalDateTime now = LocalDateTime.now();
    return MemberEntity.builder()
            .name(name)
            .nickname(nickname)
            .email(email)
            .password(password)
            .recordKey(UUID.randomUUID().toString())
            .createdAt(now)
            .updatedAt(now)
            .build();
}
```

### 4.3 업데이트 메서드
- 집계 객체를 파라미터로 받아서 내부에서 처리
```java
public void updateSummary(DailyAggregation agg) {
    this.totalSteps = agg.getSteps();
    this.totalCalories = agg.getCalories();
    this.totalDistance = agg.getDistance();
    this.updatedAt = LocalDateTime.now();
}
```

## 5. Service 규칙

### 5.1 트랜잭션 설정
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // 기본 읽기 전용
@Slf4j
public class MemberService {

    @Transactional  // 쓰기 작업시 오버라이드
    public MemberResponse signUp(SignUpRequest request) {
        // ...
    }
}
```

### 5.2 메서드 분리
- 하나의 메서드는 하나의 책임만 가짐
- private 헬퍼 메서드로 로직 분리

```java
public HealthDataSaveResponse saveHealthData(HealthDataRequest request) {
    // 메인 로직
}

private int processEntries(String recordKey, List<EntryDto> entries) {
    // 엔트리 처리 로직
}

private HealthEntryEntity parseAndSaveEntry(String recordKey, EntryDto entry) {
    // 단일 엔트리 파싱 로직
}
```

### 5.3 메서드 설명
- 모든 public/private 메서드에 간단한 Javadoc 추가
```java
/**
 * 회원가입 처리
 */
@Transactional
public MemberResponse signUp(SignUpRequest request) {
    // ...
}

/**
 * 이메일 중복 검증
 */
private void validateDuplicateEmail(String email) {
    // ...
}
```

## 6. Controller 규칙

### 6.1 ResponseUtil 사용
```java
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signUp(@RequestBody SignUpRequest request) {
        return ResponseUtil.created(memberService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseUtil.ok(memberService.login(request));
    }
}
```

### 6.2 API 문서화
```java
/**
 * 회원 API 컨트롤러
 */
@RestController
public class MemberController {

    /**
     * 회원가입
     * POST /api/member/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signUp(@RequestBody SignUpRequest request) {
        // ...
    }
}
```

## 7. 예외 처리

### 7.1 CustomException 사용
```java
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public static CustomException of(ErrorCode errorCode) {
        return new CustomException(errorCode);
    }
}
```

### 7.2 ErrorCode 정의
```java
public enum ErrorCode {
    MEMBER_EMAIL_DUPLICATE("MEMBER_001", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    MEMBER_NICKNAME_DUPLICATE("MEMBER_002", "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    MEMBER_NOT_FOUND("MEMBER_003", "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MEMBER_PASSWORD_MISMATCH("MEMBER_004", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
    // ...
}
```

## 8. 유틸리티 클래스

### 8.1 공통 로직 추출
- 여러 곳에서 사용되는 로직은 util 패키지로 분리
```java
public class DateTimeUtil {

    public static LocalDateTime parse(String dateTimeStr) {
        // 날짜/시간 파싱 로직
    }
}
```

### 8.2 집계 DTO
- 집계 데이터는 별도 DTO로 관리
```java
@Getter
public class DailyAggregation {
    private int steps = 0;
    private float calories = 0f;
    private float distance = 0f;

    public void add(int steps, float calories, float distance) {
        this.steps += steps;
        this.calories += calories;
        this.distance += distance;
    }
}
```

## 9. 로깅

### 9.1 로그 레벨
```java
@Slf4j
public class MemberService {

    public MemberResponse signUp(SignUpRequest request) {
        log.debug("회원가입 요청: email={}", request.getEmail());  // 디버그 정보

        // 처리 로직

        log.info("회원가입 완료: id={}, email={}", savedMember.getId(), savedMember.getEmail());  // 주요 이벤트
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            log.error("이메일 중복: email={}", email);  // 에러 상황
            throw CustomException.of(ErrorCode.MEMBER_EMAIL_DUPLICATE);
        }
    }
}
```

## 10. 테스트

### 10.1 Spock Framework 사용
```groovy
class MemberServiceTest extends Specification {

    MemberRepository memberRepository = Mock()
    PasswordEncoder passwordEncoder = Mock()

    @Subject
    MemberService memberService = new MemberService(memberRepository, passwordEncoder)

    def "회원가입 성공 테스트"() {
        given:
        SignUpRequest request = new SignUpRequest()
        request.name = "홍길동"
        request.email = "test@test.com"

        when:
        MemberResponse result = memberService.signUp(request)

        then:
        1 * memberRepository.existsByEmail("test@test.com") >> false
        1 * memberRepository.save(_ as MemberEntity) >> savedMember

        result.name == "홍길동"
    }
}
```

## 11. 금지 사항

1. 불필요한 주석 금지
   - `// 정적 팩토리 메서드` 같은 명백한 주석 금지
   - `/** Entity -> DTO 변환 */` 같은 단순 변환 설명 금지

2. 내부 클래스 사용 금지
   - 모든 클래스는 별도 파일로 분리

3. 하드코딩 금지
   - 에러 메시지, 상태 코드 등은 ErrorCode enum 사용

4. 매직 넘버 금지
   - 상수로 정의하여 사용
