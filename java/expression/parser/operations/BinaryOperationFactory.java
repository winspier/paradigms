package expression.parser.operations;

@FunctionalInterface
public interface BinaryOperationFactory<T> {
    AbstractBinary<T> create(MyGenericExpression<T> left, MyGenericExpression<T> right);
}
