import { expect } from 'chai'
import '../prod/datatypes'
import { integer, real } from '../prod/datatypes'

describe('integer', () => {

    it('can read/write integer numbers into raw buffers', () => {
        const buffer = new ArrayBuffer(100)
        const view = integer.view(buffer)
        for (let i = 0; i < 100 - integer.sizeInBytes; i++) {
            const n = randomInt32()
            view.set(i, n)
            expect(view.get(i)).to.equal(n)
        }
    })
})

describe('real', () => {

    it('can read/write "real" numbers into raw buffers', () => {
        const buffer = new ArrayBuffer(100)
        const view = real.view(buffer)
        for (let i = 0; i < 100 - real.sizeInBytes; i++) {
            const n = Math.random()
            view.set(i, n)
            expect(view.get(i)).to.equal(n)
        }
    })
})

function randomInt32(): number {
    return Math.round(Math.random() * (2 ** 32 - 2) - (2 ** 31))
}
