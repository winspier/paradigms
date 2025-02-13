package expression.parser.operations;

public class Divide<T> extends AbstractBinary<T> {
    public Divide(MyGenericExpression<T> left, MyGenericExpression<T> right) {
        super(left, right);
    }

    @Override
    protected T getResult(T a, T b) {
        return type.divide(a, b);
    }

    @Override
    public String getSign() {
        return "/";
    }

    @Override
    protected int getPriority() {
        return 1;
    }
}
