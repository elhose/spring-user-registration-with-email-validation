package com.js.demo.registration;

import com.js.demo.appuser.AppUser;
import com.js.demo.appuser.AppUserRole;
import com.js.demo.appuser.AppUserService;
import com.js.demo.registration.token.ConfirmationToken;
import com.js.demo.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final EmailValidator emailValidator;
    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            throw new IllegalStateException("Provided email is not valid!");
        }

        return appUserService.signUpUser(new AppUser(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                AppUserRole.USER
        ));
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationTokenByToken(token)
                                                                      .orElseThrow(() -> new IllegalStateException("Token not found!"));

        if (Objects.nonNull(confirmationToken.getConfirmedAt())) {
            throw new IllegalStateException("Email already confirmed!");
        }

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Activation token already expired!");
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        appUserService.enableAppUserByEmail(confirmationToken.getAppUser().getEmail());

        return "Email " + confirmationToken.getAppUser().getEmail() + " Confirmed!";
    }
}
