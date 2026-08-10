package joseph.com.authifyy.entities;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.stream.Collectors;

public class UserEntityWrapper implements UserDetails, OAuth2User {

    private final UserEntity user;
    private Map<String, Object> attributes;

    public UserEntityWrapper(UserEntity user) {
        this.user = user;
    }

    public UserEntityWrapper(UserEntity user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    public UserEntity getUser() {
        return user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRoles() == null) {
            return Collections.emptyList();
        }

        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map((RoleEntity roleEntity) -> new SimpleGrantedAuthority(roleEntity.getName().name()))
                .collect(Collectors.toList());

        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    // TODO: create account verification and fix it.
    public boolean isEnabled() {
        return getUser().isEnabled();
    }

    @Override
    public String getName() {
        return this.user.getName();
    }
}
