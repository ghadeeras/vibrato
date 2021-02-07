import * as exps from './expressions' 
import * as visitors from './visitors';

import binaryen from 'binaryen'

export class Assembler {

    readonly binaryCode: Uint8Array
    readonly textCode: string;

    constructor(expressions: exps.Expression[]) {
        const module = new binaryen.Module();

        const visitor = new visitors.FunctionalInterfaceVisitor(module)

        for (let exp of expressions) {
            exp.accept(visitor)
        }

        module.addFunctionExport('adder', 'adder');

        module.optimize();
        this.textCode = module.emitText();
        this.binaryCode = module.emitBinary();
        module.dispose();
    }

}
