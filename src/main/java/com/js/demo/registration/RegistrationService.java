package com.js.demo.registration;

import com.js.demo.appuser.AppUser;
import com.js.demo.appuser.AppUserRole;
import com.js.demo.appuser.AppUserService;
import com.js.demo.email.EmailSender;
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
    private final EmailSender emailSender;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            throw new IllegalStateException("Provided email is not valid!");
        }

        String token = appUserService.signUpUser(new AppUser(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                AppUserRole.USER
        ));

        emailSender.sendEmail(request.getEmail(), buildEmail(token));

        return token;
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

    private String buildEmail(String token) {
        String activationLink = "http://localhost:8080/api/v1/registration/confirm?token=" + token;
        return "<a href=\"" + activationLink + "\"> Activation Link</a>";
    }
}
