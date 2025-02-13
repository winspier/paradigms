package expression.parser.operations;

@FunctionalInterface
public interface UnaryOperationFactory<T> {
    AbstractUnary<T> create(MyGenericExpression<T> expression);
}
