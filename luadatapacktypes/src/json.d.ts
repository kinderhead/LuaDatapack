/** @noSelfInFile **/

interface Json {
    encode(this : void, obj : any) : string;
    decode(this : void, obj : string) : any;
}
