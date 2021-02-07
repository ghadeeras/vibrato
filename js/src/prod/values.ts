import * as types from './datatypes'
import * as exps from './expressions'

export class Literal<A extends types.NumberArray, S extends number> extends exps.Value<A, S> {

    private value: number[]

    private constructor(type: types.Vector<A, S>, name: string | null, value: number[]) {
        super(type, name)
        assert(() => `Expected ${type.size} vector components; found ${value.length}`, type.size == value.length)
        this.value = [...value]
    }
    
    accept<T>(visitor: exps.Visitor<T>): T {
        return visitor.visit(this)
    }
    
    delay<L extends number>(length: L): exps.Delay<A, S, L> {
        throw new Error('Method not implemented.');
    }

    get(): number[] {
        return [...this.value];
    }

    static discrete(name: string | null = null, value: number) {
        return new Literal(types.discrete, name, [value])
    }
    
    static scalar(name: string | null = null, value: number) {
        return new Literal(types.scalar, name, [value])
    }
    
    static complex(name: string | null = null, real: number, imaginary: number) {
        return new Literal(types.complex, name, [real, imaginary])
    }
    
    static vector(name: string | null = null, ...components: number[]) {
        return new Literal(types.vectorOf(components.length, types.real), name, components)
    }
    
}

function assert(message: () => string, condition: boolean) {
    if (condition) {
        throw new Error(message())
    }
}
