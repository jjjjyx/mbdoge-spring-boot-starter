package cn.mbdoge.jyx.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class POJOUtilsTest {

    @Test
    void merge() {
        A a = new A("aa", "bb", 1);
        B b = new B();

        POJOUtils.merge(a, b);

        Assertions.assertEquals(a.i, b.i);
//        Assertions.assertEquals(a.ab, b.ab);
        // b.setAb throw IOException
        Assertions.assertNull(b.ab);
        Assertions.assertNull(b.mm);
        Assertions.assertNotEquals(a, b);


        C c = new C("code1", 22, new A());
        C c2 = new C("code2", 33, new A());

        POJOUtils.merge(c, c2);

        Assertions.assertNotEquals(c, c2);
        Assertions.assertEquals(c.code, c2.code);
        Assertions.assertEquals(c.mm, c2.mm);
        Assertions.assertEquals(c.a, c2.a);

        // d not get and set
        D d = new D();

        POJOUtils.merge(d, c);
        Assertions.assertNotEquals(d, c);
        Assertions.assertNull(d.code);
        Assertions.assertEquals(d.mm, 0);

    }

    class A {
        String ab;
        String ba;
        int i;

        public A(String ab, String ba, int i) {
            this.ab = ab;
            this.ba = ba;
            this.i = i;
        }

        public A() {
        }

        public String getAb() {
            return this.ab;
        }

        public String getBa() {
            return this.ba;
        }

        public int getI() {
            return this.i;
        }

        private void setAb(String ab) {
        }

        public void setBa(String ba) {
            this.ba = ba;
        }

        public void setI(int i) {
            this.i = i;
        }

    }
    class B {
        String ab;
        String mm;
        int i;

        public B(String ab, String mm, int i) {
            this.ab = ab;
            this.mm = mm;
            this.i = i;
        }

        public B() {
        }

        public String getAb() {
            return this.ab;
        }

        public String getMm() {
            return this.mm;
        }

        public int getI() {
            return this.i;
        }

        public void setAb(String ab) throws IOException {
            throw new IOException("xxx");
//            this.ab = ab;
        }

        public void setMm(String mm) {
            this.mm = mm;
        }

        public void setI(int i) {
            this.i = i;
        }

    }
    class C {
        String code;
        int mm;
        A a;

        public C(String code, int mm, A a) {
            this.code = code;
            this.mm = mm;
            this.a = a;
        }

        public C() {
        }

        public String getCode() {
            return this.code;
        }

        public int getMm() {
            return this.mm;
        }

        public A getA() {
            return this.a;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setMm(int mm) {
            this.mm = mm;
        }

        public void setA(A a) {
            this.a = a;
        }

        @Override
        public String toString() {
            return "C{" +
                    "code='" + code + '\'' +
                    ", mm=" + mm +
                    ", a=" + a +
                    '}';
        }
    }

    class D {
        String code;
        int mm;

        public D(String code, int mm) {
            this.code = code;
            this.mm = mm;
        }

        public D() {
        }
    }
}