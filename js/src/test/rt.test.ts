import { expect } from 'chai'
import * as rt from '../prod/rt'

const rtModulesPromise = rt.initWaModulesFS("./out/wa")
const rtExportsPromise = rtModulesPromise
    .then(rtModules => rtModules.rt.exports)
    .then(exps => exps ?? error("Couldn't load Vibrato runtime!"))

describe("Runtime", async () => {

    let location = 0

    beforeEach(async () => {
        const exps = await rtExportsPromise
        exps.enter()
        location = exps.allocate8(randomInt()) - 4
    })

    afterEach(async () => {
        const exps = await rtExportsPromise
        exps.leave()
        expect(exps.allocate8(0)).to.equal(location)
    })

    describe("allocate8", () => {

        it("allocates specified number of bytes on the stack", async () => {
            const exps = await rtExportsPromise
            const size = randomInt(8)
            const ref1 = exps.allocate8(size)
            const ref2 = exps.allocate8(0)
            expect(ref2 - ref1).to.equal(size)
        })

    })

    describe("allocate16", () => {

        it("allocates specified number of 16-bit words on the stack", async () => {
            const exps = await rtExportsPromise
            const size = randomInt(8)
            const ref1 = exps.allocate16(size)
            const ref2 = exps.allocate16(0)
            expect(ref2 - ref1).to.equal(2 * size)
        })

    })

    describe("allocate32", () => {

        it("allocates specified number of 32-bit words on the stack", async () => {
            const exps = await rtExportsPromise
            const size = randomInt(8)
            const ref1 = exps.allocate32(size)
            const ref2 = exps.allocate32(0)
            expect(ref2 - ref1).to.equal(4 * size)
        })

    })

    describe("allocate64", () => {

        it("allocates specified number of 64-bit words on the stack", async () => {
            const exps = await rtExportsPromise
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

function error<T>(message: string): T {
    throw new Error(message)
}
