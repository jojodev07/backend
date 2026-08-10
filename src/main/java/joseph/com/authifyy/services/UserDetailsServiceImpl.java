package joseph.com.authifyy.services;

import joseph.com.authifyy.entities.UserEntity;
import joseph.com.authifyy.entities.UserEntityWrapper;
import joseph.com.authifyy.repositories.UserRespository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRespository userRespository;
    // requires UserDetails.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user =  userRespository.findByEmail(username)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException(String.format("User with Email: %s is not found." , username));
                });

        return new UserEntityWrapper(user);
    }
}
