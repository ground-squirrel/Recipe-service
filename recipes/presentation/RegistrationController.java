package recipes.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import recipes.business.User;
import recipes.persistence.UserRepository;

import javax.validation.Valid;

@RestController
public class RegistrationController {

    @Autowired
    UserRepository userRepo;
    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/api/register")
    @ResponseStatus(code= HttpStatus.OK)
    public void register(@Valid @RequestBody User user) {

        user.setPassword(encoder.encode(user.getPassword()));

        if(!userRepo.addUser(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

}
