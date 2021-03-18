import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void parse1() {
        Main main = new Main();
        main.parse("import io;class Fac {public int ComputeFac(int num){int num_aux ;if (num < 1 && num< 3)num_aux = 1;else num_aux =num * (this.ComputeFac(num-1));return num_aux;}public static void main(String[]args){io.println(new Fac().ComputeFac(10)[2]);}}");
    }

    @Test
    public void parse2() {
        Main main = new Main();
        main.parse("import io.ia; import ia; class Fac extends Something { public int ComputeFac(int num) { int num_aux; if (num < 1) { num_aux = 1; i = 0; i = 3; i = 2; } else { num_aux = num * (this.ComputeFac(num-1)); i = 3; i = 2; } return num_aux; } public static void main(String[] args) { int i; boolean a; i = 0; io.println(new Fac().ComputeFac(10)); i = i + 3; a[0] = a.length; while (! i < 3 ) { i = 3; i = 2; i = 1; } while (a) { i = 2; } } }");
    }
}

/*import io;

class Fac {
    public int ComputeFac(int num) {
        int num_aux;
        if (num < 1)
            num_aux = 1;
        else
            num_aux = num * (this.ComputeFac(num - 1));
        return num_aux;
    }

    public static void main(String[] args) {
        io.println(new Fac().ComputeFac(10));
    }
}*/