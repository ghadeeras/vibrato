import * as types from './datatypes' 
import * as exps from './expressions'
import * as wa from './wa';

import binaryen from 'binaryen'

export class Assembler {

    readonly binaryCode: Uint8Array
    readonly textCode: string;
    
    constructor(values: exps.Value<types.NumberArray, number>[]) {
        const module = new binaryen.Module();

        module.addMemoryImport("stack", "rt", "stack")
        module.addFunctionImport("enter", "rt", "enter", binaryen.createType([]), binaryen.none)
        module.addFunctionImport("leave", "rt", "leave", binaryen.createType([]), binaryen.none)
        module.addFunctionImport("allocate8", "rt", "allocate8", binaryen.createType([binaryen.i32]), binaryen.i32)
        module.addFunctionImport("allocate16", "rt", "allocate16", binaryen.createType([binaryen.i32]), binaryen.i32)
        module.addFunctionImport("allocate32", "rt", "allocate32", binaryen.createType([binaryen.i32]), binaryen.i32)
        module.addFunctionImport("allocate64", "rt", "allocate64", binaryen.createType([binaryen.i32]), binaryen.i32)

        for (let value of values) {
            value.declarations(module)
            const exports = value.exports();
            for (let k in exports) {
                module.addFunctionExport(exports[k], k)
            }
        }

        module.optimize();
        this.textCode = module.emitText();
        this.binaryCode = module.emitBinary();
        module.dispose();
    }

    async exports<E extends WebAssembly.Exports>(rtModulesPromise: Promise<wa.Modules>, caster: wa.Caster<E>): Promise<E> {
        return rtModulesPromise
            .then(rtModules => wa.instantiate(this.binaryCode.buffer, caster, rtModules))
    }

}
