package apps.nerdyginger.cleanplateclub;

import android.util.Log;

import androidx.annotation.NonNull;

public class Fraction {
    private int wholeNum;
    private int numerator;
    private int denominator;

    public Fraction() {
        //empty constructor
    }

    public Fraction(int wholeNum, int numerator, int denominator) {
        this.wholeNum = wholeNum;
        this.numerator = numerator;
        this.denominator = denominator;
        if (denominator == 0) {
            this.denominator = 1;
        }
    }

    public int getWholeNum() {
        return wholeNum;
    }

    public void setWholeNum(int wholeNum) {
        this.wholeNum = wholeNum;
    }

    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        this.numerator = numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        if (denominator == 0) {
            this.denominator = 1;
        } else {
            this.denominator = denominator;
        }
    }

    public boolean isMixed() {
        return wholeNum != 0;
    }

    // assumes cleaned string of format [x] [y]/[z] where
    // [x] OR [y]/[z] is optional and x, y, z are integers
    // VALID: "3", "3 1/4", "1/4"
    // INVALID: "3/", "3 1", "1/0", "3 /3", "2.5"
    public Fraction fromString(String string) {
        try {
            if (string.contains("/")) {
                String[] slashSplit = string.split("/");
                if (string.contains(" ")) {
                    // the works! whole num, numerator, and denominator
                    String[] spaceSplit = slashSplit[0].split(" ");
                    setWholeNum(Integer.parseInt(spaceSplit[0]));
                    setNumerator(Integer.parseInt(spaceSplit[1]));
                    setDenominator(Integer.parseInt(slashSplit[1]));
                } else {
                    // just a fraction with no whole number
                    setNumerator(Integer.parseInt(slashSplit[0]));
                    setDenominator(Integer.parseInt(slashSplit[1]));
                }
                simplify(); //either way
            } else {
                // no '/' means its just a whole number
                setWholeNum(Integer.parseInt(string));
            }
        } catch (Exception e) {
            Log.e("FRACTION_ERROR", "Unable to parse fraction string: " + string);
            Log.e("FRACTION_ERROR", e.toString());
        }
        return this;
    }

    public boolean isValidString(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception e) {
            if (string.matches("^(-?)*(?:(\\d+)\\s)?(\\d+)/(\\d+)$")) { //regex for mixed fraction
                return true;
            }
            return string.matches("/(?:[1-9][0-9]*|0)(?:/[1-9][0-9]*)?/g");    //regex for integers and fractions
        }
    }

    @NonNull
    public String toString() {
        String string = "";
        if (wholeNum == 0 && numerator == 0) {
            return "0";
        }
        if (wholeNum != 0) {
            string += wholeNum;
        }
        if (wholeNum != 0 && numerator != 0) {
            string += " ";
        }
        if (numerator != 0) {
            string += numerator + "/" + denominator;
        }
        return string;
    }

    public Fraction add(Fraction other) {
        Fraction sum = new Fraction();

        int selfNum = (denominator * wholeNum) + numerator;
        int otherNum = (other.getDenominator() * other.getWholeNum()) + other.getNumerator();
        int lcm = denominator;

        if (denominator != other.getDenominator()) {
            lcm = getLCM(denominator, other.getDenominator());
            if (denominator != lcm) {
                int multiplicand = lcm / denominator;
                selfNum *= multiplicand;
            }
            if (other.getDenominator() != lcm) {
                int multiplicand = lcm / other.getDenominator();
                otherNum *= multiplicand;
            }
        }
        sum.setNumerator(selfNum + otherNum);
        sum.setDenominator(lcm);
        sum.simplify();

        return sum;
    }

    public Fraction subtract(Fraction other) {
        Fraction difference = new Fraction();

        int selfNum = (denominator * wholeNum) + numerator;
        int otherNum = (other.getDenominator() * other.getWholeNum()) + other.getNumerator();
        int lcm = denominator;

        if (denominator != other.getDenominator()) {
            lcm = getLCM(denominator, other.getDenominator());
            if (denominator != lcm) {
                int multiplicand = lcm / denominator;
                selfNum *= multiplicand;
            }
            if (other.getDenominator() != lcm) {
                int multiplicand = lcm / other.getDenominator();
                otherNum *= multiplicand;
            }
        }
        difference.setNumerator(selfNum - otherNum);
        difference.setDenominator(lcm);
        difference.simplify();

        return difference;
    }

    public Fraction multiply(Fraction other) {
        Fraction product = new Fraction();

        product.setNumerator(numerator * other.getNumerator());
        product.setDenominator(denominator * other.getDenominator());
        product.simplify();

        return product;
    }

    public Fraction divide(Fraction other) {
        Fraction dividend = new Fraction();

        dividend.setNumerator(numerator * other.getDenominator());
        dividend.setDenominator(denominator * other.getNumerator());
        dividend.simplify();

        return dividend;
    }

    public void simplify() {
        if (denominator == 0) {
            setNumerator(0);
            setDenominator(1);
            return;
        }
        int intDivResult = numerator / denominator;
        setWholeNum(wholeNum + intDivResult);
        setNumerator( numerator % denominator);
        int gcd = getGCD(numerator, denominator);
        setNumerator(numerator / gcd);
        setDenominator(denominator / gcd);
    }

    private int getGCD(int a, int b) {
        if (a % b == 0) {
            return b;
        } else {
            return getGCD(b, a % b);
        }
    }

    private int getLCM(int a, int b) {
        return (a * b) / getGCD(a, b);
    }


}
