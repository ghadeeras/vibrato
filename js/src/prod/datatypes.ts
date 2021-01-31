export type PrimitiveSize = 1 | 2 | 4 | 8

export interface Primitive<S extends PrimitiveSize> {

    sizeInBytes: S

    view(buffer: ArrayBuffer): PrimitiveView

}

export interface Vector<P extends Primitive<any>, S extends number> {

    primitiveType: P
    
    size: S

    sizeInBytes: number

    view(buffer: ArrayBuffer): PrimitiveView

}

export interface PrimitiveView {

    get(firstBytePosition: number): number

    set(firstBytePosition: number, value: number): void

    getComponent(component: number, firstBytePosition: number): number

    setComponent(component: number, firstBytePosition: number, value: number): void

}

abstract class AbstractView implements PrimitiveView {

    private dataView: DataView
    
    public constructor(
        buffer: ArrayBuffer, 
        private readonly primitiveType: Primitive<PrimitiveSize>
    ) {
        this.dataView = new DataView(buffer)
    }
    
    get(firstBytePosition: number): number {
        return this.getComponent(0, firstBytePosition)
    }

    set(firstBytePosition: number, value: number): void {
        this.setComponent(0, firstBytePosition, value)
    }

    getComponent(component: number, firstBytePosition: number): number {
        return this.getAtOffset(this.dataView, this.primitiveType, this.offset(component, firstBytePosition));
    }

    setComponent(component: number, firstBytePosition: number, value: number): void {
        this.setAtOffset(this.dataView, this.primitiveType, this.offset(component, firstBytePosition), value);
    }

    private offset(component: number, firstBytePosition: number): number {
        return component * this.primitiveType.sizeInBytes + firstBytePosition
    }

    abstract getAtOffset(dataView: DataView, primitiveType: Primitive<PrimitiveSize>, offset: number): number

    abstract setAtOffset(dataView: DataView, primitiveType: Primitive<PrimitiveSize>, offset: number, value: number): void

}

class IntegerView extends AbstractView {

    getAtOffset(dataView: DataView, primitiveType: Primitive<PrimitiveSize>, offset: number): number {
        switch (primitiveType.sizeInBytes) {
            case 1: return dataView.getInt8(offset)
            case 2: return dataView.getInt16(offset)
            case 4: return dataView.getInt32(offset)
            case 8: return dataView.getInt32(offset)
        }
    }

    setAtOffset(dataView: DataView, primitiveType: Primitive<PrimitiveSize>, offset: number, value: number): void {
        switch (primitiveType.sizeInBytes) {
            case 1: return dataView.setInt8(offset, value)
            case 2: return dataView.setInt16(offset, value)
            case 4: return dataView.setInt32(offset, value)
            case 8: return dataView.setInt32(offset, value)
        }
    }

}

class FloatView extends AbstractView {

    getAtOffset(dataView: DataView, primitiveType: Primitive<PrimitiveSize>, offset: number): number {
        switch (primitiveType.sizeInBytes) {
            case 1: throw new Error("8-bit floats are not supported!")
            case 2: throw new Error("16-bit floats are not supported!")
            case 4: return dataView.getFloat32(offset)
            case 8: return dataView.getFloat64(offset)
        }
    }

    setAtOffset(dataView: DataView, primitiveType: Primitive<PrimitiveSize>, offset: number, value: number): void {
        switch (primitiveType.sizeInBytes) {
            case 1: throw new Error("8-bit floats are not supported!")
            case 2: throw new Error("16-bit floats are not supported!")
            case 4: return dataView.setFloat32(offset, value)
            case 8: return dataView.setFloat64(offset, value)
        }
    }

}

export class Integer implements Primitive<4> {

    private constructor() {
    }
    
    get sizeInBytes(): 4 {
        return 4
    }

    view(buffer: ArrayBuffer): PrimitiveView {
        return new IntegerView(buffer, this)
    }

    static readonly type = new Integer()

}

export class Real implements Primitive<8> {

    private constructor() {
    }
    
    get sizeInBytes(): 8 {
        return 8
    }

    view(buffer: ArrayBuffer): PrimitiveView {
        return new FloatView(buffer, this)
    }

    static readonly type = new Real()

}

export const integer = Integer.type
export const real = Real.type

class GenericVector<P extends Primitive<any>, S extends number> implements Vector<P, S> {
    
    constructor(readonly primitiveType: P, readonly size: S) {}

    get sizeInBytes(): number {
        return this.primitiveType.sizeInBytes * this.size
    }

    view(buffer: ArrayBuffer): PrimitiveView {
        return this.primitiveType.view(buffer)
    }

}

export function vectorOf<P extends Primitive<any>, S extends number>(size: S, primitiveType: P): Vector<P, S> {
    return new GenericVector(primitiveType, size)
}

export class Discrete extends GenericVector<Integer, 1> {

    private constructor() {
        super(integer, 1)
    }

    static readonly type = new Discrete()

}

export class Scalar extends GenericVector<Real, 1> {

    private constructor() {
        super(real, 1)
    }

    static readonly type = new Scalar()

}

export class Complex extends GenericVector<Real, 2> {

    private constructor() {
        super(real, 2)
    }

    static readonly type = new Complex()

}

export const discrete = Discrete.type
export const scalar = Scalar.type
export const complex = Complex.type
