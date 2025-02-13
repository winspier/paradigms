package expression.parser.operations;

public class Negate<T> extends AbstractUnary<T> {
    public Negate(MyGenericExpression<T> expression) {
        super(expression);
    }

    @Override
    protected String getSign() {
        return "-";
    }

    @Override
    protected T getResult(T a) {
        return type.negate(a);
    }

}
