package ru.college.carmarketplace.model.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.college.carmarketplace.enums.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponse {
  private String name;
  private Integer id;
  private String email;
  private Role role;
  private String accessToken;
  private String refreshToken;
  private String phoneNumber;
}
