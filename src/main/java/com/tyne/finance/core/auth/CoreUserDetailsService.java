package com.tyne.finance.core.auth;

import com.tyne.finance.core.models.User;
import com.tyne.finance.core.repositories.CoreUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoreUserDetailsService implements UserDetailsService {
    private final CoreUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.repository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username + " not found");
        }
        return new CoreUserDetails(user);
    }
}
