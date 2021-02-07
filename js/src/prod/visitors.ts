import * as types from './datatypes'
import * as exps from './expressions'
import * as values from './values'

import binaryen from 'binaryen'

type InsType = binaryen.Module["i32"] | binaryen.Module["f64"]

export class FunctionalInterfaceVisitor implements exps.Visitor<binaryen.FunctionRef> {

    constructor(private module: binaryen.Module) {
    }

    private visitValue<A extends types.NumberArray, S extends number>(
        exp: exps.Value<A, S>, 
        primitiveCase: (dataType: binaryen.Type, insType: InsType) => binaryen.ExpressionRef[],
        vectorCase: (dataType: binaryen.Type, insType: InsType) => binaryen.ExpressionRef[]
    ): binaryen.FunctionRef {
        const [dataType, insType] = exp.type.primitiveType == types.integer ? 
            [binaryen.i32, this.module.i32] : 
            [binaryen.f64, this.module.f64]
        
        const label = `${exp.name}_body`
        const fun = exp.type.size == 1 ?
            this.module.addFunction(exp.name, binaryen.createType([]), dataType, [], 
                this.block(label, 
                    primitiveCase(dataType, insType)
                )
            ) :
            this.module.addFunction(exp.name, binaryen.createType([binaryen.i32]), binaryen.i32, [], 
                this.block(label,[ 
                    ...vectorCase(dataType, insType),
                    this.module.local.get(0, binaryen.i32)
                ])
            )
        if (exp.visible) {
            this.module.addFunctionExport(exp.name, exp.name)
        }
        return fun
    }

    private block(label: string, expressions: binaryen.ExpressionRef[]) {
        return expressions.length > 1 ? this.module.block(label, expressions, binaryen.i32) : expressions[0]
    }
    
    visit<A extends types.NumberArray, S extends number>(exp: values.Literal<A, S>): binaryen.FunctionRef {
        const vector = exp.get()
        return this.visitValue(exp,
            (dataType, insType) => [insType.const(vector[0])],
            (dataType, insType) => [...components(exp.type, i => 
                insType.store(i * exp.type.primitiveType.sizeInBytes, 0,
                    this.module.local.get(0, binaryen.i32),
                    insType.const(vector[i])
                )
            )]
        )
    }

}

function *components<A extends types.NumberArray, S extends number, T>(vectorType: types.Vector<A, S>, mapper: (component: number) => T) {
    for (let i = 0; i < vectorType.size; i++) {
        yield mapper(i)
    }
}