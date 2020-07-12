package com.trunk.idp.component.impl;

import com.trunk.idp.component.ReactiveUserDetailsEnhancedPasswordService;
import com.trunk.idp.component.UserDetailsEnhanced;
import com.trunk.idp.component.UserDetailsEnhancedChecker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public abstract class AbstractUserDetailsEnhancedReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    protected final Log logger = LogFactory.getLog(getClass());

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private ReactiveUserDetailsEnhancedPasswordService userDetailsPasswordService;

    private Scheduler scheduler = Schedulers.boundedElastic();

    private final UserDetailsEnhancedChecker preAuthenticationChecks = user -> {
        if (!user.isAccountNonLocked()) {
            logger.debug("User account is locked");

            throw new LockedException(this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.locked",
                    "User account is locked"));
        }

        if (!user.isEnabled()) {
            logger.debug("User account is disabled");

            throw new DisabledException(this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.disabled",
                    "User is disabled"));
        }

        if (!user.isAccountNonExpired()) {
            logger.debug("User account is expired");

            throw new AccountExpiredException(this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.expired",
                    "User account has expired"));
        }
    };

    private UserDetailsEnhancedChecker postAuthenticationChecks = user -> {
        if (!user.isCredentialsNonExpired()) {
            logger.debug("User account credentials have expired");

            throw new CredentialsExpiredException(this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.credentialsExpired",
                    "User credentials have expired"));
        }
    };

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        final String username = authentication.getName();
        final String presentedPassword = (String) authentication.getCredentials();
        return retrieveUser(username)
                .doOnNext(this.preAuthenticationChecks::check)
                .publishOn(this.scheduler)
                .filter(u -> this.passwordEncoder.matches(presentedPassword, u.getPassword()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid Credentials"))))
                .flatMap(u -> {
                    boolean upgradeEncoding = this.userDetailsPasswordService != null
                            && this.passwordEncoder.upgradeEncoding(u.getPassword());
                    if (upgradeEncoding) {
                        String newPassword = this.passwordEncoder.encode(presentedPassword);
                        return this.userDetailsPasswordService.updatePassword(u, newPassword);
                    }
                    return Mono.just(u);
                })
                .doOnNext(this.postAuthenticationChecks::check)
                .map(u -> new UsernamePasswordAuthenticationToken(u, u.getPassword(), u.getAuthorities()));
    }

    /**
     * The {@link PasswordEncoder} that is used for validating the password. The default is
     * {@link PasswordEncoderFactories#createDelegatingPasswordEncoder()}
     *
     * @param passwordEncoder the {@link PasswordEncoder} to use. Cannot be null
     */
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Sets the {@link Scheduler} used by the {@link UserDetailsRepositoryReactiveAuthenticationManager}.
     * The default is {@code Schedulers.newParallel(String)} because modern password encoding is
     * a CPU intensive task that is non blocking. This means validation is bounded by the
     * number of CPUs. Some applications may want to customize the {@link Scheduler}. For
     * example, if users are stuck using the insecure {@link org.springframework.security.crypto.password.NoOpPasswordEncoder}
     * they might want to leverage {@code Schedulers.immediate()}.
     *
     * @param scheduler the {@link Scheduler} to use. Cannot be null.
     * @since 5.0.6
     */
    public void setScheduler(Scheduler scheduler) {
        Assert.notNull(scheduler, "scheduler cannot be null");
        this.scheduler = scheduler;
    }

    /**
     * Sets the service to use for upgrading passwords on successful authentication.
     *
     * @param userDetailsPasswordService the service to use
     */
    public void setUserDetailsPasswordService(ReactiveUserDetailsEnhancedPasswordService userDetailsPasswordService) {
        this.userDetailsPasswordService = userDetailsPasswordService;
    }

    /**
     * Sets the strategy which will be used to validate the loaded <tt>UserDetails</tt>
     * object after authentication occurs.
     *
     * @param postAuthenticationChecks The {@link UserDetailsEnhancedChecker}
     * @since 5.2
     */
    public void setPostAuthenticationChecks(UserDetailsEnhancedChecker postAuthenticationChecks) {
        Assert.notNull(this.postAuthenticationChecks, "postAuthenticationChecks cannot be null");
        this.postAuthenticationChecks = postAuthenticationChecks;
    }

    /**
     * Allows subclasses to retrieve the <code>UserDetails</code>
     * from an implementation-specific location.
     *
     * @param username The username to retrieve
     * @return the user information. If authentication fails, a Mono error is returned.
     */
    protected abstract Mono<UserDetailsEnhanced> retrieveUser(String username);

}

