package Muscle.auth.security;

public interface AuthToken<T> {
    boolean validate();
    T getClaims();
}
