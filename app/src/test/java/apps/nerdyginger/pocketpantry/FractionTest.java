package apps.nerdyginger.pocketpantry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FractionTest {
    @Test
    public void setFractionTest() {
        Fraction fraction = new Fraction();
        fraction.setWholeNum(3);
        fraction.setNumerator(1);
        fraction.setDenominator(4);
        assertEquals("3 1/4", fraction.toString());

        Fraction f2 = new Fraction(3, 1, 4);
        assertEquals("3 1/4", f2.toString());
    }

    @Test
    public void fromStringTest() {
        Fraction f1 = new Fraction();
        f1.fromString("2 4/5");
        Fraction f2 = new Fraction();
        f2.fromString("3/4");
        Fraction f3 = new Fraction();
        f3.fromString("5");

        //f1 values
        assertEquals(2, f1.getWholeNum());
        assertEquals(4, f1.getNumerator());
        assertEquals(5, f1.getDenominator());

        //f2 values
        assertEquals(0, f2.getWholeNum());
        assertEquals(3, f2.getNumerator());
        assertEquals(4, f2.getDenominator());

        //f3 values
        assertEquals(5, f3.getWholeNum());
        assertEquals(0, f3.getNumerator());
        assertEquals(0, f3.getDenominator());
    }

    @Test
    public void addFractionTest() {
        Fraction f1 = new Fraction(0, 12, 10);
        Fraction f2 = new Fraction(1, 2, 5);
        Fraction sum = f1.add(f2);
        assertEquals("2 3/5", sum.toString());

        f1 = new Fraction(500, 0, 0);
        f2 = new Fraction(0, 0, 1);
        sum = f1.add(f2);
        assertEquals("500", sum.toString());
    }

    @Test
    public void subtractFractionTest() {
        Fraction f1 = new Fraction(40, 2, 16);
        Fraction f2 = new Fraction(40, 2, 16);
        Fraction d1 = f1.subtract(f2);
        assertEquals("0", d1.toString());

        Fraction f3 = new Fraction(5, 1, 5);
        Fraction f4 = new Fraction(0, 2, 5);
        Fraction d2 = f3.subtract(f4);
        assertEquals("4 4/5", d2.toString());

        Fraction f5 = new Fraction(65, 0, 0);
        Fraction f6 = new Fraction(2, 1, 2);
        Fraction d3 = f5.subtract(f6);
        assertEquals("62 1/2", d3.toString());

        Fraction f0 = new Fraction(0, 0, 0);
        Fraction d4 = f6.subtract(f0);
        assertEquals("2 1/2", d4.toString());
    }

    @Test
    public void isValidStringTest() {
        Fraction f1 = new Fraction();
        assertTrue(f1.isValidString("2 2/3"));
        assertTrue(f1.isValidString("2"));
        assertTrue(f1.isValidString("500 200/10000"));
        assertTrue(f1.isValidString("3/99"));
        assertFalse(f1.isValidString("/3"));
        assertFalse(f1.isValidString(" 3 "));
        assertFalse(f1.isValidString("3 2/"));
    }

    @Test
    public void multiplyTest() {
        Fraction f1 = new Fraction(500, 0, 0);
        Fraction f2 = new Fraction(1, 0, 0);
        Fraction product = f1.multiply(f2);
        assertEquals("500", product.toString());
    }

    @Test
    public void divideTest() {
        Fraction f1 = new Fraction(500, 0, 0 );
        Fraction f2 = new Fraction(1000, 0, 0);
        Fraction d1 = f1.divide(f2);
        assertEquals("1/2", d1.toString());

        f1 = new Fraction(100, 0, 0);
        f2 = new Fraction(1, 0, 0);
        Fraction d2 = f1.divide(f2);
        assertEquals("100", d2.toString());
    }
}
