"use strict"

const expressions = (function () {
    function AbstractOperation(...operands) {
        this.operands = operands;
    }

    AbstractOperation.prototype = Object.create({
        evaluate: function (...vars) {
            return this.operate(...this.operands.map(operand => operand.evaluate(...vars)));
        },
        toString: function () {
            return this.operands.map(x => x.toString()).join(" ") + " " + this.getSign();
        },
        diff: function (variable) {
            return this.partDiff(...this.operands.map(x => Object.create({diff: x.diff(variable), expr: x})));
        },
        prefix: function () {
            return `(${this.getSign()} ${this.operands.map(x => x.prefix()).join(" ")})`;
        },
        postfix: function () {
            return `(${this.operands.map(x => x.postfix()).join(" ")} ${this.getSign()})`;
        }
    });
    const OPERATIONS = {};

    function OperationBuilder(cntArgs, operate, sign, partDiff) {
        let operation = function (...args) {
            AbstractOperation.call(this, ...args);
        };
        operation.prototype = Object.create(AbstractOperation.prototype);
        operation.prototype.operate = operate;
        operation.prototype.getSign = () => sign;
        operation.prototype.partDiff = partDiff;
        operation.prototype.cntArgs = cntArgs;
        OPERATIONS[sign] = {constructor: operation, cntArgs: cntArgs};
        return operation;
    }

    const Subtract = OperationBuilder(2, (a, b) => a - b, "-",
        (left, right) => new Subtract(left.diff, right.diff)
    );

    const Add = OperationBuilder(2, (a, b) => a + b, "+",
        (left, right) => new Add(left.diff, right.diff)
    );

    const Multiply = OperationBuilder(2, (a, b) => a * b, "*",
        (left, right) => sumPartDiffs([left, right])
    );

    const Divide = OperationBuilder(2, (a, b) => a / b, "/",
        (left, right) => new Divide(
            new Subtract(
                new Multiply(left.diff, right.expr),
                new Multiply(left.expr, right.diff)
            ),
            new Square(right.expr)
        )
    );

    const Hypot = OperationBuilder(2, (a, b) => a * a + b * b, "hypot",
        (left, right) => new Multiply(
            Const.TWO,
            new Add(
                new Multiply(left.expr, left.diff),
                new Multiply(right.expr, right.diff)
            )
        )
    );

    const HMean = OperationBuilder(2, (a, b) => 2 / (1 / a + 1 / b), "hmean",
        (left, right) => new Multiply(
            Const.TWO,
            new Divide(
                sumPartDiffs([left, right], p => new Square(p.expr)),
                new Square(new Add(left.expr, right.expr))
            )
        )
    );

    const sumExpressions = (args) => args.reduce((a, b) => new Add(a, b));
    const replacedProduct = (array, getter, replacedId, replacedValue) => array.reduce((acc, p, j) =>
        new Multiply(acc, j === replacedId ? replacedValue : getter(p)), Const.ONE)
    const sumPartDiffs = (parts, getExpr = p => p.expr, getDiff = p => p.diff) =>
        sumExpressions(parts.map((part, i) => replacedProduct(parts, getExpr, i, getDiff(part))));

    const ArithMean = OperationBuilder(Infinity, (...args) => args.reduce((a, b) => a + b) / args.length,
        "arithMean",
        (...parts) => new Divide(sumExpressions(parts.map(x => x.diff)), new Const(parts.length))
    );

    const GeomMean = OperationBuilder(Infinity, (...args) => Math.abs(args.reduce((a, b) => a * b)) ** (1 / args.length),
        "geomMean",
        (...parts) => new Divide(
            new Multiply(
                sumPartDiffs(parts),
                new Pow(replacedProduct(parts, p => p.expr), new Const(1 / parts.length - 1))
            ),
            new Const(parts.length)
        )
    );

    const Pow = OperationBuilder(2, (a, p) => a ** p, "pow",
        (expr, pow) => new Multiply(new Const(pow.expr), new Pow(expr.diff, pow.expr - 1))
    );

    const HarmMean = OperationBuilder(Infinity, (...args) => args.length / args.map(x => 1 / x).reduce((a, b) => a + b),
        "harmMean",
        (...parts) => new Divide(
            new Multiply(new Const(parts.length), sumPartDiffs(parts, p => new Square(p.expr))),
            new Square(sumPartDiffs(parts, p => p.expr, () => Const.ONE))
        )
    );

    const Square = OperationBuilder(1, a => a * a, "square",
        part => new Multiply(Const.TWO, new Multiply(part.diff, part.expr))
    );

    const Negate = OperationBuilder(1, a => -a, "negate",
        (part) => new Negate(part.diff)
    );

    function AbstractValue(value) {
        this.value = value;
    }
    AbstractValue.prototype.toString = function() { return this.value.toString() };
    AbstractValue.prototype.prefix = AbstractValue.prototype.toString;
    AbstractValue.prototype.postfix = AbstractValue.prototype.toString;

    function Const(value) { AbstractValue.call(this, value); }
    Const.prototype = Object.create(AbstractValue.prototype);
    Const.prototype.evaluate = function () { return this.value; }
    Const.prototype.diff = () => Const.ZERO;

    Const.ZERO = new Const(0);
    Const.ONE = new Const(1);
    Const.TWO = new Const(2);

    const VARIABLES = {
        "x": 0,
        "y": 1,
        "z": 2
    };

    function Variable(name) { AbstractValue.call(this, name); }
    Variable.prototype = Object.create(AbstractValue.prototype);
    Variable.prototype.evaluate = function (...vars) { return vars[VARIABLES[this.value]]; }
    Variable.prototype.diff = function (v) { return v === this.value ? Const.ONE : Const.ZERO; }

    return {
        Subtract: Subtract,
        Multiply: Multiply,
        Add: Add,
        Divide: Divide,
        Negate: Negate,
        Const: Const,
        Variable: Variable,
        VARIABLES: VARIABLES,
        OPERATIONS: OPERATIONS,
        Hypot: Hypot,
        HMean: HMean,
        Square: Square,
        ArithMean: ArithMean,
        GeomMean: GeomMean,
        HarmMean: HarmMean,
    };
})();

const expressionParser = (function () {
    function ParserErrorBuilder(name) {
        let ParserError = function (message) {
            this.name = name;
            this.message = message;
        }
        ParserError.prototype = Object.create(Error.prototype);
        ParserError.prototype.constructor = ParserError;
        return ParserError;
    }

    const EndOfExpressionError = ParserErrorBuilder("EndOfExpressionError");
    const UnsupportedTokenError = ParserErrorBuilder("UnsupportedTokenError");
    const InvalidBracketSequenceError = ParserErrorBuilder("InvalidBracketSequence");
    const UnknownOperationError = ParserErrorBuilder("UnknownOperationError");
    const InvalidArgumentsError = ParserErrorBuilder("InvalidArgumentsError");
    const EmptyExpressionError = ParserErrorBuilder("EmptyExpressionError");

    const parse = (expr) => {
        expr = expr.trim().split(/\s+/);
        let stack = []
        for (let pos = 0; pos < expr.length; pos++) {
            const token = expr[pos];
            if (token in OPERATIONS) {
                stack.push(new OPERATIONS[token].constructor(...stack.splice(-OPERATIONS[token].cntArgs)))
            } else if (token in VARIABLES) {
                stack.push(new Variable(String(token)));
            } else {
                stack.push(new Const(+token));
            }
        }
        return stack[0];
    };

    function parsePreAndPostfix(expression, postfixMode = false) {
        const tokens = expression.match(/[()]|[^ ()]+/g);
        if (tokens === null) {
            throw new EmptyExpressionError("Empty input");
        }
        let position = 0;
        let currentToken;

        function parseNextToken() {
            position++;
            currentToken = tokens.shift();
            if (currentToken === undefined) {
                throw new EndOfExpressionError("Cannot found next token");
            }
            return currentToken;
        }
        const test = (expected) => tokens.length !== 0 && tokens[0] === expected;
        function parseExpression() {
            const token = parseNextToken();
            if (token === '(') {
                if (tokens.length === 0 || test(')')) {
                    throw new EmptyExpressionError("Empty expression");
                }
                return parseBracket();
            } else if (token in VARIABLES) {
                return new Variable(token);
            } else if (!isNaN(+token)) {
                return new Const(+token);
            } else if (token === ')') {
                throw new InvalidBracketSequenceError("Unexpected closing parenthesis");
            } else if (token in OPERATIONS) {
                throw new InvalidArgumentsError("Cannot find arguments");
            } else {
                throw new UnsupportedTokenError("Unknown token");
            }
        }
        function parseBracket() {
            let args = [];
            let operator;
            if (!postfixMode) {
                operator = parseNextToken();
                if (!(operator in OPERATIONS)) {
                    throw new UnknownOperationError("Expected operation");
                }
                while (args.length < OPERATIONS[operator].cntArgs && !test(')')) {
                    args.push(parseExpression());
                }
            } else {
                do {
                    operator = parseExpression();
                    args.push(operator);
                    if (test(")")) {
                        throw new UnknownOperationError("Cannot find operation");
                    }
                } while(!(tokens[0] in OPERATIONS));
                operator = parseNextToken();
            }
            if (OPERATIONS[operator].cntArgs !== Infinity && args.length !== OPERATIONS[operator].cntArgs) {
                throw new InvalidArgumentsError("Unexpected number of arguments");
            }
            if (tokens.length === 0 || parseNextToken() !== ')') {
                throw new InvalidBracketSequenceError("Missed closing parenthesis");
            }
            if (args.length === 0) {
                throw new InvalidArgumentsError("Missed arguments");
            }
            return new OPERATIONS[operator].constructor(...args);
        }

        const res = parseExpression();
        if (tokens.length > 0)
            throw new EndOfExpressionError("Expected end");
        return res;
    }

    return {
        parse: parse,
        parsePrefix: expression => parsePreAndPostfix(expression),
        parsePostfix: expression => parsePreAndPostfix(expression, true)
    };
})();


const Subtract = expressions.Subtract;
const Multiply = expressions.Multiply;
const Add = expressions.Add;
const Divide = expressions.Divide;
const Negate = expressions.Negate;
const Const = expressions.Const;
const Variable = expressions.Variable;
const VARIABLES = expressions.VARIABLES;
const OPERATIONS = expressions.OPERATIONS;
const Hypot = expressions.Hypot;
const HMean = expressions.HMean;
const parse = expressionParser.parse;
const parsePrefix = expressionParser.parsePrefix;
const parsePostfix = expressionParser.parsePostfix;
const ArithMean = expressions.ArithMean;
const GeomMean = expressions.GeomMean;
const HarmMean = expressions.HarmMean;
