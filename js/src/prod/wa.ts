import fs from 'fs'

export type Caster<E extends WebAssembly.Exports> = (exports: WebAssembly.Exports) => E;

export type Module<E extends WebAssembly.Exports> = {
    readonly sourceFile: string;
    readonly caster: Caster<E>;
    exports?: E; 
}

export type Modules = Readonly<Record<string, Module<WebAssembly.Exports>>>

export type ModuleName<M extends Modules> = keyof M;

export function module<E extends WebAssembly.Exports>(sourceFile: string, caster: Caster<E>): Module<E> {
    return {
        sourceFile: sourceFile,
        caster: caster
    }
}

export async function loadWeb<M extends Modules>(waPath: string, modules: M, first: ModuleName<M>, ...rest: ModuleName<M>[]): Promise<M> {
    const firstModule = modules[first]
    const response = await fetch(waPath + "/" + firstModule.sourceFile, { method : "get", mode : "no-cors" })
    const buffer = await response.arrayBuffer()
    const waModule = await WebAssembly.instantiate(buffer, asImports(modules))
    firstModule.exports = firstModule.caster(waModule.instance.exports)
    return rest.length == 0 ? modules : loadWeb(waPath, modules, rest[0], ...rest.slice(1))
}

export async function loadFS<M extends Modules>(waPath: string, modules: M, first: ModuleName<M>, ...rest: ModuleName<M>[]): Promise<M> {
    const firstModule = modules[first]
    const buffer = fs.readFileSync(waPath + "/" + firstModule.sourceFile)
    const waModule = await WebAssembly.instantiate(buffer, asImports(modules))
    firstModule.exports = firstModule.caster(waModule.instance.exports)
    return rest.length == 0 ? modules : loadFS(waPath, modules, rest[0], ...rest.slice(1))
}

function asImports<M extends Modules>(modules: M): WebAssembly.Imports {
    const imports: WebAssembly.Imports = {};
    for (let key in modules) {
        imports[key] = modules[key].exports || {};
    }
    return imports;
}