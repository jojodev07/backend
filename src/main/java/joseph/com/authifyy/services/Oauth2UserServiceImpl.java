package joseph.com.authifyy.services;

import joseph.com.authifyy.entities.*;
import joseph.com.authifyy.repositories.RoleRepository;
import joseph.com.authifyy.repositories.UserRespository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class Oauth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserRespository userRespository;
    private final RoleRepository roleRepository;

    public Oauth2UserServiceImpl(UserRespository userRespository,
                                 RoleRepository roleRepository) {
        this.userRespository = userRespository;
        this.roleRepository = roleRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String clientRegistrationId = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = oAuth2User.getName();

        //I return the user whether they're new or old.
        UserEntity user = userRespository.findByEmail(email) // returns the user and does nothing if user actually exists.
                        .orElseGet(() -> {
                            UserEntity newUser = new UserEntity();
                            newUser.setEmail(email);
                            newUser.setName(name);
                            //IMPORTANT: oauth2 users don't need verification.
                            newUser.setEnabled(true);

                            if (clientRegistrationId.equals("google")) newUser.setProvider(ProviderType.PROVIDER_GOOGLE);

                            RoleEntity userRole = roleRepository.findByName(RoleType.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role USER was not found in the database."));
                            newUser.getRoles().add(userRole);

                            return userRespository.save(newUser);
                        });

        // TODO: #IMPORTANT! failureHandler needed ASAP.
        if (user.getProvider().equals(ProviderType.PROVIDER_LOCAL)) {
            throw new OAuth2AuthenticationException("Email belongs to a local user, and account merging is not available.");
        }

        return new UserEntityWrapper(user, attributes);
    }

}
