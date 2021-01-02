package com.snreloaded;

public class Triple<A, B, C> {
    private A thing1;
    private B thing2;
    private C thing3;

    public Triple(A thing1, B thing2, C thing3) {
        this.thing1 = thing1;
        this.thing2 = thing2;
        this.thing3 = thing3;
    }

    public A getA() {
        return thing1;
    }

    public void setA(A thing1) {
        this.thing1 = thing1;
    }

    public B getB() {
        return thing2;
    }

    public void setB(B thing2) {
        this.thing2 = thing2;
    }

    public C getC() {
        return thing3;
    }

    public void setC(C thing3) {
        this.thing3 = thing3;
    }

    @Override
    public boolean equals(Object o) {
        if ( o == this ) {
            return true;
        } else if ( !(o instanceof Triple) ) {
            return false;
        }
        Triple other = (Triple) o;
        return this.getA() == other.getA() && this.getB() == other.getB();
    }

    @Override
    public String toString() {
        return "{" + thing1 +
                " : " + thing2 +
                " : " + thing3 +
                '}';
    }
}
