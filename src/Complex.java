/**
 * Complex.java
 * A class for Complex numbers
 * @author William Hemminger
 * 8 March 2021
 */

import static java.lang.Math.sqrt;

public class Complex {
    public double real;
    public double imaginary;

    Complex()
    {
        this.real = 0;
        this.imaginary = 0;
    }

    Complex(double r, double i)
    {
        this.real = r;
        this.imaginary = i;
    }

    public Complex add(Complex c)
    {
        double r = this.real + c.real;
        double i = this.imaginary + c.imaginary;

        return new Complex(r, i);
    }

    public Complex sub(Complex c)
    {
        double r = this.real - c.real;
        double i = this.imaginary - c.imaginary;

        return new Complex(r, i);
    }

    public Complex prod(Complex c)
    {
        double first = this.real * c.real;
        double outer = this.real * c.imaginary;
        double inner = this.imaginary * c.real;
        double last = this.imaginary * c.imaginary * -1;

        double r = first + last;
        double i = outer + inner;

        return new Complex(r, i);
    }

    public Complex sqr()
    {
        double first = this.real * this.real;
        double outer = this.real * this.imaginary;
        double inner = this.imaginary * this.real;
        double last = this.imaginary * this.imaginary * -1;

        double r = first + last;
        double i = outer + inner;

        return new Complex(r, i);
    }

    public double abs()
    {
        return sqrt((this.real * this.real) + (this.imaginary * this.imaginary));
    }

    @Override
    public String toString()
    {
        return this.real + " + " + this.imaginary + "i";
    }
}
