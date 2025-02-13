package expression.parser.operations;

public class Count<T> extends AbstractUnary<T> {
    public Count(MyGenericExpression<T> expression) {
        super(expression);
    }

    @Override
    protected String getSign() {
        return "count";
    }

    @Override
    protected T getResult(T a) {
        return type.bitCount(a);
    }
}
