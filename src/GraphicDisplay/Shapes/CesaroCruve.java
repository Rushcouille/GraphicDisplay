package GraphicDisplay.Shapes;

import GraphicDisplay.Tools.Complex;
import GraphicDisplay.Tools.Parameter;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Random;

public class CesaroCruve extends Shape {

    public Parameter n = new Parameter(1, 1, 1, "Number of steps");
    private Parameter ax = new Parameter(2, (float)0.5, 0.001, "Affine parameter real part");
    private Parameter ay = new Parameter(3, (float)0.5, 0.001, "Affine parameter imaginary part");
    private Complex a = new Complex(ax.value, ay.value);

    public CesaroCruve(){
        coordinates = new double[]{400, 600};
    }

    /**
     * Draws on the Graphics2D the point in the complex plan.
     * @param points the list of points to print
     */
    private void drawComplex(ArrayList<Complex> points){
        Line2D l = new Line2D.Double(0, 0, 0, 0);
        Ellipse2D e = new Ellipse2D.Double(0,0,0,0);
        double d = 0.0001;
        int ind = 0;
        int size = points.size();
        Color c = new Color(4, 255, 255);
        Color k = new Color(255,0, 240);
        float kr = (float)k.getRed()/255;
        float kg = (float)k.getGreen()/255;
        float kb = (float)k.getBlue()/255;
        float dr = (float)(c.getRed()-k.getRed())/size/255;
        float dg = (float)(c.getGreen()-k.getGreen())/size/255;
        float db = (float)(c.getBlue()-k.getBlue())/size/255;
        for (Complex z : points){
            double re = z.re(); double im = z.im();
            e.setFrame(re, -im, 0.001, 0.001);
                g2.setColor(new Color(kr+ind*dr, kg+ind*dg, kb+ind*db));
            g2.fill(e);
            ind++;
        }
    }


    @Override
    protected void saveImageToBufferedImage(Graphics2D g2d) {

        float f = this.n.value;
        this.n.value = 16;
        super.saveImageToBufferedImage(g2d);
        this.n.value = f;

    }

    /**
     * Creates a boolean table that will identify the order in which the functions will be call to build the next step.
     */
    private boolean[] functionOrder(){
        boolean res[] = new boolean[(int)n.value];
        Random rand = new Random();
        for (int i = 0; i < n.value; i++){
            res[i] = rand.nextBoolean();
        }
        return res;
    }

    /**
     * Build the sequence of points to print using the "chaos game" technique. (slower)
     */
    private ArrayList<Complex> IFSresult (ArrayList<Complex> sequence){

        boolean[] order = functionOrder();
        int sequenceSize;
        int index;
        Complex nextValue;

        for (int i = 0; i < n.value; i++){

            sequenceSize = sequence.size();
            index = 0;
            if (order[i]){
                while (index < sequenceSize){
                    nextValue = d0(sequence.get(index));
                    sequence.add(nextValue);
                    index++;
                }
            } else {
                while (index < sequenceSize){
                    nextValue = d1(sequence.get(index));
                    sequence.add(nextValue);
                    index++;
                }
            }
        }
        return sequence;
    }

    /**
     * Build the sequence by taking a point and then iterating every possible combination of function (do and d1).
     * (faster)
     */
    private ArrayList<Complex> IFSresult2 (Complex z){
        ArrayList<Complex> list = new ArrayList<>();
        list.add(z);
        int sequenceSize;
        Complex c, s;

        for (int i = 0; i < n.value; i++){

            sequenceSize = list.size();

            for (int j = 0; j < sequenceSize; j++){
                c = list.remove(j);
                s = d0(c);
                c = d1(c);
                list.add(j, c);
                list.add(s);
            }
        }

        return list;
    }

    /**
     * First function we are using to build the sequence.
     */
    private Complex d0 (Complex z){
        Complex z2 = new Complex(z.re(), z.im());
        z2.times(a);
        return z2;
    }

    /**
     * Second function we are using to build he sequence.
     */
    private Complex d1 (Complex z){
        Complex z2 = new Complex(z.re(), z.im());
        Complex A = new Complex(1-a.re(), -a.im());
        z2.times(A);
        z2.plus(a);
        return z2;
    }

    @Override
    protected void drawShape() {
        System.out.println("Processing : " + n.value);
        g2.scale(600, 600);
        g2.setStroke(new BasicStroke((float)0.001));
        a.set(ax.value, ay.value);
        g2.setColor(Color.BLACK);
        g2.fillRect(-10, -10, 20, 20);

        Complex z1 = new Complex(0.5, 0.5);

        ArrayList<Complex> finalSequence = IFSresult2(z1);

        drawComplex(finalSequence);
        System.out.println("Done");

    }

    @Override
    public Parameter[] parameters() {
        return new Parameter[]{n, ax, ay};
    }

    @Override
    public void changeParameter(Parameter parameter) {
        if (parameter.id == 1){
            n = parameter;
        } else if (parameter.id == 2){
            ax = parameter;
        } else if (parameter.id == 3){
            ay = parameter;
        }
    }
}
