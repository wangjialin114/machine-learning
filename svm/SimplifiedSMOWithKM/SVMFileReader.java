package SimplifiedSMOWithKM;

/**
 * Created by Wang on 2016/12/6.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class SVMFileReader {
    String filename;
    FileInputStream fis;
    double[][] x;
    int[] y;

    public SVMFileReader(String filename) {
        super();
        File file = new File(filename);
        try {
            file = new File(file.getCanonicalPath());
            //System.out.println();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    private HeartLine getSVMDataLine(BufferedReader br) {
        String strs[];
        double[] curX = null;
        int y = 0;
        try {
            strs = br.readLine().split(" ");
            curX = new double[10];

            if (strs[0].equals("+1")) {
                y = 1;
            } else {
                y = -1;
            }
            for (int i = 1; i < 11; i++) {
                String s[] = strs[i].split(":");
                curX[i - 1] = Double.parseDouble(s[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HeartLine(curX, y);
    }
    public class HeartLine{
        double[] x;
        int y;

        public HeartLine(double[] x, int y){
            this.x = x;
            this.y = y;
        }
    }
    public void getSVMData(int lines) {
        this.x = new double[lines][];
        this.y = new int[lines];
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        for (int i = 0; i < lines; i++) {
            HeartLine hl = getSVMDataLine(br);
            this.x[i] = hl.x;
            this.y[i] = hl.y;
        }
    }

}
