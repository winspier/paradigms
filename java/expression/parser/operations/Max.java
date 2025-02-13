package expression.parser.operations;

public class Max<T> extends AbstractBinary<T> implements HalfAssociative {
    public Max(MyGenericExpression<T> left, MyGenericExpression<T> right) {
        super(left, right);
    }

    @Override
    public String getSign() {
        return "max";
    }

    @Override
    protected int getPriority() {
        return 3;
    }

    @Override
    protected T getResult(T a, T b) {
        return type.max(a, b);
    }
}
