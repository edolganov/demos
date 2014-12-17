package demo.util.reflections;


public final class NoOpTypeParameterMatcher extends TypeParameterMatcher {
    @Override
    public boolean match(Object msg) {
        return true;
    }
}
