package com.example.oath2.user;

import com.example.oath2.security.JWTService;
import com.example.oath2.security.PasswordEncoderConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoderConfig passwordEncoder;
    private final JWTService jwtService;

    /**
     * this method creates a user and adds it to the database.
     * @param username the username of the user
     * @param password the password of the user
     * @throws IllegalArgumentException if the username, password is null or empty or if the username is already picked.
     * @return returns the newly created user.
     */

    public User createUser(String username, String password) {
        if(username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username can not be empty.");
        }

        if(password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password may not be empty.");
        }

        if(userRepository.findUserByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username is already chosen, please pick another one.");
        }

        password = passwordEncoder.passwordEncoder().encode(password);

        User user = new User(username,password);
        return userRepository.save(user);
    }

    /**
     * This method signs in the user and returns a jwt token
     * @param username the username of the user
     * @param password the password of the user
     * @throws IllegalArgumentException if the username or password is null or empty. if the account doesnt exist and if the password doesnt match with the username
     * @return returns a newly generated jwt token.
     */

    public String loginUser(String username, String password) {
        if(username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is empty.");
        }

        if(password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is empty.");
        }

        if(userRepository.findUserByUsername(username).isEmpty()) {
            throw new IllegalArgumentException("Account doesn't exist.");
        }

        User user = userRepository.findUserByUsername(username).get();

        if(!passwordEncoder.passwordEncoder().matches(password,user.getPassword())) {
            throw new IllegalArgumentException("Wrong credentials.");
        }

        return jwtService.generateToken(user.getId());
    }

    /**
     * A method of an existing method in repository is here so other classes can use it without needing access to repository
     * It finds user by UUId
     * @param userId the UUID of the user.
     * @return returns the optional<User>
     */

    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    /**
     * Verifies the jwt token and finds the user with the id from the token.
     * @param token the jwt token the user inputs
     * @return returns the user found by the jwt token id.
     */

    public Optional<User> verifyAuthentication(String token) {
        try{
            UUID userId = jwtService.verifyToken(token);
            return findById(userId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByOpenId(String id) {
        return userRepository.findByOidcId(id);
    }

    public User createOpenIdUser(String username, String oidcId) {
        User user = new User(username, null);
        user.setOidcId(oidcId);
        user.setOidcProvider("github");
        return userRepository.save(user);
    }

    /**
     * loads user based on username.
     * @param username the username of the user
     * @return returns the user found by the username or throws exception
    */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}
