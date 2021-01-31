import { assert } from 'chai';
import * as datatypes from './datatypes'

export interface Expression {
}

export interface DataExpression<V extends datatypes.Vector<any, any>> extends Expression {

    type: V

    evaluate(): number[] | null

}

export interface Function<I extends Expression, O extends Expression> extends Expression {

    apply(input: I): O

}

export interface Delay<V extends datatypes.Vector<any, any>, L extends number> extends Expression {

    length: L

    at(index: DataExpression<datatypes.Discrete>): DataExpression<V>

}

export class Literal<V extends datatypes.Vector<any, any>> implements DataExpression<V> {

    private value: number[]

    private constructor(readonly type: V, value: number[]) {
        assert(type.size == value.length)
        this.value = [...value]
    }

    evaluate(): number[] {
        return [...this.value];
    }

    static discrete(value: number) {
        return new Literal(datatypes.discrete, [value])
    }
    
    static scalar(value: number) {
        return new Literal(datatypes.scalar, [value])
    }
    
    static complex(real: number, imaginary: number) {
        return new Literal(datatypes.complex, [real, imaginary])
    }
    
    static vector(...components: number[]) {
        return new Literal(datatypes.vectorOf(components.length, datatypes.real), components)
    }
    
}
