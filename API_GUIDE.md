# API 개발 가이드

이 문서는 Weather Application의 API 개발 시 사용하는 공통 응답 구조, 예외 처리, Swagger 문서화에 대한 가이드입니다.

---

## 📋 목차

1. [공통 응답 구조](#1-공통-응답-구조)
2. [예외 처리](#2-예외-처리)
3. [Swagger 문서화](#3-swagger-문서화)

---

## 1. 공통 응답 구조

모든 API는 일관된 응답 형식을 사용합니다.

### 📌 응답 형식

```json
{
  "code": "SUCCESS",
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": { ... }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `code` | String | 응답 코드 (성공: "SUCCESS", 실패: 에러 코드) |
| `message` | String | 응답 메시지 |
| `data` | Generic | 실제 데이터 (없을 경우 null) |

### 📌 ApiResponse 클래스

위치: `com.example.weather.global.response.ApiResponse`

```java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String code;
    private String message;
    private T data;
}
```

### 📌 사용 방법

#### 1) 성공 응답 (데이터 있음)

```java
@GetMapping("/users/{id}")
public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
    UserResponse user = userService.findById(id);
    return ApiResponse.success(user);
}
```

**응답 예시:**
```json
{
  "code": "SUCCESS",
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": {
    "id": 1,
    "name": "홍길동",
    "email": "hong@example.com"
  }
}
```

#### 2) 성공 응답 (데이터 없음)

```java
@DeleteMapping("/users/{id}")
public ApiResponse<Void> deleteUser(@PathVariable Long id) {
    userService.delete(id);
    return ApiResponse.success();
}
```

**응답 예시:**
```json
{
  "code": "SUCCESS",
  "message": "요청이 성공적으로 처리되었습니다."
}
```

#### 3) 실패 응답

실패 응답은 예외를 던지면 GlobalExceptionHandler가 자동으로 처리합니다.
직접 반환하는 경우:

```java
return ApiResponse.error("E001", "사용자를 찾을 수 없습니다.");
```

**응답 예시:**
```json
{
  "code": "E001",
  "message": "사용자를 찾을 수 없습니다."
}
```

---

## 2. 예외 처리

### 📌 ErrorCode 정의

위치: `com.example.weather.global.error.ErrorCode`

에러 코드는 Enum으로 관리되며, HttpStatus, 코드, 메시지를 포함합니다.

```java
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),

    // Resource
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "요청한 리소스를 찾을 수 없습니다."),

    // Authentication & Authorization
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A004", "권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
```

#### 에러 코드 네이밍 규칙

| 접두사 | 카테고리 | 예시 |
|--------|----------|------|
| C | Common (공통) | C001, C002, C003 |
| R | Resource (리소스) | R001, R002 |
| A | Authentication/Authorization | A001, A002, A003 |
| U | User (사용자) | U001, U002 |
| W | Weather (날씨) | W001, W002 |

### 📌 BusinessException 사용법

위치: `com.example.weather.global.exception.BusinessException`

비즈니스 로직에서 발생하는 예외를 처리하기 위한 커스텀 예외입니다.

#### 1) 기본 사용

```java
@Service
public class UserService {

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
```

**응답 예시 (404 Not Found):**
```json
{
  "code": "R001",
  "message": "요청한 리소스를 찾을 수 없습니다."
}
```

#### 2) 커스텀 메시지 사용

```java
public void deleteUser(Long id) {
    User user = findById(id);
    if (user.isAdmin()) {
        throw new BusinessException(
            ErrorCode.FORBIDDEN,
            "관리자 계정은 삭제할 수 없습니다."
        );
    }
    userRepository.delete(user);
}
```

**응답 예시 (403 Forbidden):**
```json
{
  "code": "A004",
  "message": "관리자 계정은 삭제할 수 없습니다."
}
```

#### 3) 원인 예외 포함

```java
try {
    // 외부 API 호출
    weatherClient.fetchData();
} catch (Exception e) {
    throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, e);
}
```

### 📌 GlobalExceptionHandler

위치: `com.example.weather.global.exception.GlobalExceptionHandler`

모든 예외를 자동으로 처리하여 일관된 응답을 반환합니다.

#### 처리하는 예외 목록

| 예외 | HTTP Status | 설명 |
|------|-------------|------|
| `BusinessException` | ErrorCode에 정의된 상태 | 비즈니스 로직 예외 |
| `MethodArgumentNotValidException` | 400 Bad Request | @Valid 검증 실패 |
| `HttpRequestMethodNotSupportedException` | 405 Method Not Allowed | HTTP 메서드 불일치 |
| `MethodArgumentTypeMismatchException` | 400 Bad Request | 파라미터 타입 불일치 |
| `AccessDeniedException` | 403 Forbidden | 접근 권한 없음 |
| `MissingServletRequestParameterException` | 400 Bad Request | 필수 파라미터 누락 |
| `HttpMessageNotReadableException` | 400 Bad Request | 잘못된 요청 형식 |
| `Exception` | 500 Internal Server Error | 그 외 모든 예외 |

#### Validation 예외 처리 예시

```java
@Data
public class CreateUserRequest {
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Min(value = 0, message = "나이는 0 이상이어야 합니다.")
    private Integer age;
}

@PostMapping("/users")
public ApiResponse<UserResponse> createUser(
    @Valid @RequestBody CreateUserRequest request) {
    // 검증 실패 시 자동으로 400 에러 반환
    return ApiResponse.success(userService.create(request));
}
```

**검증 실패 시 응답 예시:**
```json
{
  "code": "C002",
  "message": "잘못된 입력값입니다.",
  "data": {
    "name": "이름은 필수입니다.",
    "email": "올바른 이메일 형식이 아닙니다.",
    "age": "나이는 0 이상이어야 합니다."
  }
}
```

### 📌 커스텀 에러 코드 추가하기

#### 1단계: ErrorCode에 새로운 코드 추가

```java
public enum ErrorCode {
    // 기존 코드들...

    // Weather (날씨)
    WEATHER_API_ERROR(HttpStatus.BAD_GATEWAY, "W001", "날씨 API 호출에 실패했습니다."),
    INVALID_LOCATION(HttpStatus.BAD_REQUEST, "W002", "유효하지 않은 위치 정보입니다.");
}
```

#### 2단계: 비즈니스 로직에서 사용

```java
@Service
public class WeatherService {

    public WeatherInfo getWeather(String location) {
        if (!isValidLocation(location)) {
            throw new BusinessException(ErrorCode.INVALID_LOCATION);
        }

        try {
            return weatherClient.fetch(location);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.WEATHER_API_ERROR, e);
        }
    }
}
```

---

## 3. Swagger 문서화

### 📌 Swagger UI 접속

#### 로컬 환경
```
http://localhost:8080/swagger-ui.html
```

#### 운영 환경
```
https://cotato-weather.o-r.kr/swagger-ui.html
```

#### OpenAPI 스펙
```
http://localhost:8080/api-docs
```

### 📌 기본 설정

위치: `com.example.weather.global.config.SwaggerConfig`

- **타이틀**: Weather API
- **버전**: 1.0.0
- **인증**: Bearer Token (JWT)
- **서버**: Local, Production

### 📌 컨트롤러 문서화

#### 1) 기본 컨트롤러 문서화

```java
@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Operation(
        summary = "사용자 조회",
        description = "ID로 사용자 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(
        @Parameter(description = "사용자 ID", example = "1")
        @PathVariable Long id
    ) {
        return ApiResponse.success(userService.findById(id));
    }
}
```

#### 2) 요청/응답 DTO 문서화

```java
@Schema(description = "사용자 생성 요청")
@Data
public class CreateUserRequest {

    @Schema(description = "사용자 이름", example = "홍길동", required = true)
    @NotBlank
    private String name;

    @Schema(description = "이메일 주소", example = "hong@example.com", required = true)
    @Email
    private String email;

    @Schema(description = "나이", example = "25", minimum = "0")
    @Min(0)
    private Integer age;
}

@Schema(description = "사용자 응답")
@Data
public class UserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "이메일 주소", example = "hong@example.com")
    private String email;
}
```

#### 3) 인증이 필요한 API 문서화

```java
@Operation(
    summary = "사용자 정보 수정",
    description = "현재 로그인한 사용자의 정보를 수정합니다.",
    security = @SecurityRequirement(name = "bearerAuth")
)
@PutMapping("/me")
public ApiResponse<UserResponse> updateMe(
    @RequestBody UpdateUserRequest request,
    @AuthenticationPrincipal UserDetails userDetails
) {
    return ApiResponse.success(userService.update(userDetails.getUsername(), request));
}
```

#### 4) 에러 응답 문서화

```java
@Operation(
    summary = "사용자 삭제",
    description = "사용자를 삭제합니다. 관리자 권한이 필요합니다."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "삭제 성공"
    ),
    @ApiResponse(
        responseCode = "403",
        description = "권한 없음",
        content = @Content(
            schema = @Schema(implementation = ApiResponse.class),
            examples = @ExampleObject(
                value = "{\"code\":\"A004\",\"message\":\"권한이 없습니다.\"}"
            )
        )
    ),
    @ApiResponse(
        responseCode = "404",
        description = "사용자를 찾을 수 없음"
    )
})
@DeleteMapping("/{id}")
public ApiResponse<Void> deleteUser(@PathVariable Long id) {
    userService.delete(id);
    return ApiResponse.success();
}
```

### 📌 주요 어노테이션

| 어노테이션 | 사용 위치 | 설명 |
|-----------|----------|------|
| `@Tag` | Controller 클래스 | API 그룹 정의 |
| `@Operation` | Controller 메서드 | API 설명 |
| `@ApiResponses` | Controller 메서드 | 응답 코드별 설명 |
| `@Parameter` | 메서드 파라미터 | 파라미터 설명 |
| `@Schema` | DTO 클래스/필드 | 스키마 설명 |
| `@SecurityRequirement` | Controller 메서드 | 인증 필요 명시 |

### 📌 실전 예제

#### Health Check Controller

```java
@Tag(name = "Health Check", description = "서버 상태 확인 API")
@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    @PersistenceContext
    private EntityManager entityManager;

    @Operation(
        summary = "서버 상태 확인",
        description = "서버가 정상적으로 실행 중인지 확인합니다."
    )
    @GetMapping
    public ApiResponse<HealthCheckResponse> healthCheck() {
        HealthCheckResponse response = new HealthCheckResponse(
            "OK",
            "Server is running",
            LocalDateTime.now()
        );
        return ApiResponse.success(response);
    }

    @Operation(
        summary = "데이터베이스 연결 확인",
        description = "MySQL 데이터베이스 연결 상태를 확인합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "DB 연결 성공"),
        @ApiResponse(responseCode = "500", description = "DB 연결 실패")
    })
    @GetMapping("/db")
    public ApiResponse<DatabaseHealthResponse> databaseHealthCheck() {
        try {
            String version = (String) entityManager
                .createNativeQuery("SELECT VERSION()")
                .getSingleResult();

            String database = (String) entityManager
                .createNativeQuery("SELECT DATABASE()")
                .getSingleResult();

            DatabaseHealthResponse response = new DatabaseHealthResponse(
                "OK",
                "Database connection successful",
                database,
                version,
                LocalDateTime.now()
            );
            return ApiResponse.success(response);
        } catch (Exception e) {
            DatabaseHealthResponse response = new DatabaseHealthResponse(
                "ERROR",
                "Database connection failed: " + e.getMessage(),
                null,
                null,
                LocalDateTime.now()
            );
            return ApiResponse.error("DB_CONNECTION_ERROR", "데이터베이스 연결 실패", response);
        }
    }
}
```

---

## 📚 참고 자료

- **Springdoc OpenAPI**: https://springdoc.org/
- **Spring Boot Validation**: https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#validation
- **Spring Exception Handling**: https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc

---

## 💡 Best Practices

### 1. 에러 코드 관리
- 에러 코드는 항상 ErrorCode Enum에 정의하여 사용
- 카테고리별로 접두사를 통일하여 관리
- 명확하고 구체적인 에러 메시지 작성

### 2. 예외 처리
- 비즈니스 로직 예외는 항상 BusinessException 사용
- 직접 ResponseEntity를 반환하지 말고 ApiResponse 사용
- 예외 발생 시 로그를 남겨 추적 가능하도록 구현

### 3. Swagger 문서화
- 모든 Controller에 @Tag 추가
- 모든 API에 @Operation 추가하여 명확한 설명 제공
- DTO 필드에 @Schema로 예시값 제공
- 가능한 모든 응답 코드를 @ApiResponses로 문서화

### 4. Validation
- DTO에 @Valid 어노테이션 사용
- 명확한 검증 메시지 작성 (message 속성 활용)
- 비즈니스 로직 검증은 Service 계층에서 수행
