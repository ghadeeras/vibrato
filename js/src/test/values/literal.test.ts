import { expect } from 'chai'
import { Literal } from '../../prod/values/literal'
import * as asm from '../../prod/assembler'
import * as rt from '../../prod/rt'
import * as types from '../../prod/datatypes'

type TestExports = {
    pi: () => number,
    five: () => number,
    complex: (ref: number) => number,
    vector: (ref: number) => number,
}

const assembler = new asm.Assembler([
    Literal.scalar(3.14).named("pi"),
    Literal.discrete(5).named("five"),
    Literal.complex(0.7, 0.5).named("complex"),
    Literal.vector(1.2, 2.3, 3.4).named("vector")        
])

console.log(assembler.textCode)

const rtModulesPromise = rt.initWaModulesFS("./out/wa")
const rtExportsPromise = rtModulesPromise
    .then(rtModules => rtModules.rt.exports)
    .then(exp => exp ?? error("Couldn't load Vibrato runtime!"))
const testExportsPromise = assembler.exports(rtModulesPromise, exports => exports as TestExports)

describe("Literal", async () => {

    it("returns literal scalar values", async () => {
        const testModules = await testExportsPromise
        expect(testModules.pi()).to.equal(3.14)
    })

    it("returns literal discrete values", async () => {
        const testModules = await testExportsPromise
        expect(testModules.five()).to.equal(5)
    })

    it("returns literal complex values", async () => {
        const rtExports = await rtExportsPromise
        const testModules = await testExportsPromise
        const ref = testModules.complex(rtExports.allocate64(2))
        const view = types.complex.view(rtExports.stack.buffer, ref)[0]
        expect(view.length).to.equal(2)
        expect(view[0]).to.equal(0.7)
        expect(view[1]).to.equal(0.5)
    })

    it("returns literal vector values", async () => {
        const rtExports = await rtExportsPromise
        const testModules = await testExportsPromise
        const ref = testModules.vector(rtExports.allocate64(3))
        const view = types.vectorOf(3, types.real).view(rtExports.stack.buffer, ref)[0]
        expect(view.length).to.equal(3)
        expect(view[0]).to.equal(1.2)
        expect(view[1]).to.equal(2.3)
        expect(view[2]).to.equal(3.4)
    })

})

function error<T>(message: string): T {
    throw new Error(message)
}
