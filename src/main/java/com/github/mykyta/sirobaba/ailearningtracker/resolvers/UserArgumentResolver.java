package com.github.mykyta.sirobaba.ailearningtracker.resolvers;

import com.github.mykyta.sirobaba.ailearningtracker.annotations.CurrentUser;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;

/**
 * Argument resolver responsible for injecting the currently authenticated user's
 * information into controller method parameters annotated with {@link CurrentUser}.
 * <p>
 * This resolver extracts the authenticated principal from the current request
 * and delegates loading of user details to {@link UserService}.
 * <p>
 * It allows controllers to receive {@link CurrentUserInfoDto} directly without
 * manually interacting with security context or JWT parsing.
 *
 * <pre>
 *     @GetMapping("/me")
 *     public UserResponseDto getCurrentUser(@CurrentUser CurrentUserInfoDto user) {
 *         return user; // resolved automatically
 *     }
 * </pre>
 */
@Component
@AllArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserService userService;

    /**
     * Determines whether the method parameter is supported by this resolver.
     * <p>
     * A parameter is eligible if:
     * <ul>
     *     <li>It is annotated with {@link CurrentUser}</li>
     *     <li>Its type is {@link CurrentUserInfoDto}</li>
     * </ul>
     *
     * @param parameter the method parameter to check
     * @return true if the parameter should be resolved by this resolver
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null
               && parameter.getParameterType().equals(CurrentUserInfoDto.class);
    }

    /**
     * Resolves the method parameter into a {@link CurrentUserInfoDto} object.
     * <p>
     * Logic:
     * <ul>
     *     <li>Extract the {@link Principal} from the incoming request</li>
     *     <li>If the principal exists, load the user info using {@link UserService}</li>
     *     <li>If the principal is null (unauthenticated request), returns null</li>
     * </ul>
     *
     * @param parameter     the method parameter
     * @param mavContainer  container (unused)
     * @param webRequest    current HTTP request
     * @param binderFactory binder factory (unused)
     * @return resolved CurrentUserInfoDto or null if no principal is present
     */
    @Override
    public Object resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Principal principal = webRequest.getUserPrincipal();
        return principal != null
                ? userService.findCurrentUserInfoDto(principal.getName())
                : null;
    }
}

