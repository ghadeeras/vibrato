import * as wa from "./wa.js"

export type Reference = number;

export type RuntimeExports = {
    
    stack: WebAssembly.Memory;

    enter: () => void;
    leave: () => void;
    allocate8: (size: number) => Reference;
    allocate16: (size: number) => Reference;
    allocate32: (size: number) => Reference;
    allocate64: (size: number) => Reference;

}

export const modules = {
    rt: wa.module("rt.wasm", exports => exports as RuntimeExports)
}

type RuntimeModules = typeof modules

export async function initWaModulesWeb(waPath: string) {
    return wa.loadWeb(waPath, modules, "rt");
}

export async function initWaModulesFS(waPath: string) {
    return wa.loadFS(waPath, modules, "rt");
}
