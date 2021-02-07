#!bash

function command() {
    echo "${WAT_FILE%.*}"
}

echo "Making sure Web Assmebly output directory exists ..."
mkdir ./out
rm -R ./out/wa 
mkdir ./out/wa || exit 1 

echo "Building Web Assembly modules ..."
ls ./src/wa/*.wat \
    | xargs -I {} basename {} ".wat" \
    | xargs -I {} wat2wasm --output=./out/wa/{}.wasm ./src/wa/{}.wat || exit 1

echo "Success!"
