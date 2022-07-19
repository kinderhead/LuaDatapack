package mod.kinderhead.util;

public class Out<T> {
    T obj = null;

    public Out() {}

    public void Set(T obj) {
        this.obj = obj;
    }

    public T Get() {
        return obj;
    }
}
