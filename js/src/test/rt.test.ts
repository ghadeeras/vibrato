import { fail } from 'assert'
import { expect } from 'chai'
import * as rt from '../prod/rt'

describe("Runtime", async () => {

    const modules = await rt.initWaModulesFS("./out/wa")
    const exps = modules.rt.exports
    if (!exps) {
        fail()
    }

    let location = 0

    beforeEach(() => {
        exps.enter()
        location = exps.allocate8(randomInt()) - 4
    })

    afterEach(() => {
        exps.leave()
        expect(exps.allocate8(0)).to.equal(location)
    })

    describe("allocate8", () => {

        it("allocates specified number of bytes on the stack", () => {
            const size = randomInt(8)
            const ref1 = exps.allocate8(size)
            const ref2 = exps.allocate8(0)
            expect(ref2 - ref1).to.equal(size)
        })

    })

    describe("allocate16", () => {

        it("allocates specified number of 16-bit words on the stack", () => {
            const size = randomInt(8)
            const ref1 = exps.allocate16(size)
            const ref2 = exps.allocate16(0)
            expect(ref2 - ref1).to.equal(2 * size)
        })

    })

    describe("allocate32", () => {

        it("allocates specified number of 32-bit words on the stack", () => {
            const size = randomInt(8)
            const ref1 = exps.allocate32(size)
            const ref2 = exps.allocate32(0)
            expect(ref2 - ref1).to.equal(4 * size)
        })

    })

    describe("allocate64", () => {

        it("allocates specified number of 64-bit words on the stack", () => {
            const size = randomInt(8)
            const ref1 = exps.allocate64(size)
            const ref2 = exps.allocate64(0)
            expect(ref2 - ref1).to.equal(8 * size)
        })

    })

})

function randomInt(min: number = 0, max: number = min + 100): number {
    return Math.round((max - min) * Math.random() + min)
}
