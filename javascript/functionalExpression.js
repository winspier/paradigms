"use strict"

const cnst = a => () => a
const applyOperation = operation => (...operands) => (...variables) => operation(...operands.map(operand => operand(...variables)))
const reduceOperation = operation => applyOperation( (...args) => args.reduce(operation))
const subtract = applyOperation((a, b) => a - b)
const multiply = applyOperation((a, b) => a * b)
const add = applyOperation((a, b) => a + b)
const divide = applyOperation((a, b) => a / b)
const negate = applyOperation(a => -a)
const min = reduceOperation(Math.min)
const max = reduceOperation(Math.max)

const min5 = applyOperation((a, b, c, d, e) => min(a, b, c, d, e))
const max3 = applyOperation((a, b, c) => max(a, b, c))
const avg5 = applyOperation((a, b, c, d, e) => (a + b + c + d + e) / 5)
const med3 = applyOperation((a, b, c) => {
    let arr = [a, b, c]
    arr.sort((q, w) => q - w)
    return arr[1]
})


const variable = (name) => (...args) => args[variables[name]]

const variables = {
    "x": 0,
    "y": 1,
    "z": 2
};

const constants = {
    "pi": cnst(Math.PI),
    "e": cnst(Math.E)
};

const pi = () => Math.PI;
const e = () => Math.E;

const operators = {
    "*": {apply: multiply, cnt: 2},
    "/": {apply: divide, cnt: 2},
    "+": {apply: add, cnt: 2},
    "-": {apply: subtract, cnt: 2},
    "negate": {apply: negate, cnt: 1},
    "min5": {apply: min5, cnt: 5},
    "max3": {apply: max3, cnt: 3},
    "avg5": {apply: avg5, cnt: 5},
    "med3": {apply: med3, cnt: 3}
}

let testExpr = add (
    add(
        multiply(
            variable("x"),
            variable("x")
        ),
        multiply(
            cnst(-2),
            variable("x")
        )
    ),
    cnst(1)
)

const test = () => {
    for (let i = 0; i <= 10; i++) {
        println("f(" + i + ") = " + testExpr(i))
    }
}

const parse = (expr) => {
    expr = expr.split(/\s+/).filter(s => s.length > 0);
    let stack = []
    for (const q of expr) {
        if (q in operators) {
            stack.splice(-operators[q].cnt, operators[q].cnt, operators[q].apply(...stack.slice(-operators[q].cnt) ));
        } else if (q in variables) {
            stack.push(variable(String(q)))
        } else if (q in constants) {
            stack.push(constants[q])
        } else {
            stack.push(cnst(Number(q)))
        }
    }
    return stack[0]
}