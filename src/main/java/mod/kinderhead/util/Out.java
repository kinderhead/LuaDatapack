package mod.kinderhead.util;

public class Out<T> {
    private T obj = null;

    public Out() {}

    public void set(T obj) {
        this.obj = obj;
    }

    public T get() {
        return obj;
    }
}
