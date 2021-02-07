import * as types from './datatypes'
import * as values from './values'

let nameSequencer = 0

export interface Expression {

    accept<T>(visitor: Visitor<T>): T

}

export abstract class Value<A extends types.NumberArray, S extends number> implements Expression {

    readonly name: string
    readonly visible: boolean
    
    constructor(readonly type: types.Vector<A, S>, name: string | null) {
        if (name && name.charAt(0) != '_') {
            this.name = `${name}`
            this.visible = true
        } else {
            this.name = name ? name : `_${nameSequencer++}`
            this.visible = false
        }
    }
    
    abstract accept<T>(visitor: Visitor<T>): T

    abstract get(): number[] | null

    delay<L extends number>(length: L): Delay<A, S, L> {
        throw new Error('Method not implemented.');
    }

}

export interface Function<I extends Expression, O extends Expression> extends Expression {

    apply(input: I): O

}

export interface Delay<A extends types.NumberArray, S extends number, L extends number> extends Expression {

    length: L

    value: Value<A, S>

    at(index: Value<Int32Array, 1>): Value<A, S>

}

export interface Visitor<T> {

    visit<A extends types.NumberArray, S extends number>(exp: values.Literal<A, S>): T;

}