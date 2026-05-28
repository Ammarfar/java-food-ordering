package ammarfar.test.food.ordering.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ammarfar.test.food.ordering.Dto.ApiResponse;
import ammarfar.test.food.ordering.Dto.LoginRequest;
import ammarfar.test.food.ordering.Dto.LoginResponse;
import ammarfar.test.food.ordering.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication endpoints")
@Validated
public class AuthController {

  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Login user", description = "Generate JWT token using email and password")
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully Login"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request invalid"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Wrong Credential")
  })
  public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
    return ApiResponse.of("Login successful",
        new LoginResponse(
            userService.login(request.email(), request.password())));
  }
}
