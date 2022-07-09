class test {
    private _obj : Vec3d = {x:0, y:0, z:0};

    get obj() : Vec3d {
        return this._obj;
    }

    set obj(v : Vec3d) {
        say("Edit")
        this._obj = v;
    }
}

let t = new test();

t.obj.x = 4;
