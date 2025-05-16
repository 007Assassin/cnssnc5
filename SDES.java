import java.util.*;

public class SDES {
    static int[] P10 = {3,5,2,7,4,10,1,9,8,6};
    static int[] P8 = {6,3,7,4,8,5,10,9};
    static int[] IP = {2,6,3,1,4,8,5,7};
    static int[] IPi = {4,1,3,5,7,2,8,6};
    static int[] EP = {4,1,2,3,2,3,4,1};
    static int[] P4 = {2,4,3,1};

    static int[][] S0 = {
        {1,0,3,2},
        {3,2,1,0},
        {0,2,1,3},
        {3,1,3,2}
    };

    static int[][] S1 = {
        {0,1,2,3},
        {2,0,1,3},
        {3,0,1,0},
        {2,1,0,3}
    };

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter 10-bit key: ");
        String key = sc.next();
        if (key.length() != 10 || !key.matches("[01]+")) {
            System.out.println("Invalid key. Please enter a 10-bit binary string.");
            sc.close();
            return;
        }

        System.out.print("Enter 8-bit plaintext: ");
        String pt = sc.next();
        if (pt.length() != 8 || !pt.matches("[01]+")) {
            System.out.println("Invalid plaintext. Please enter an 8-bit binary string.");
            sc.close();
            return;
        }

        int[][] keys = genKeys(key);
        int[] ptBits = toBits(pt);
        int[] cipherText = encrypt(ptBits, keys);
        int[] decryptedText = decrypt(cipherText, keys);

        System.out.print("Cipher: ");
        print(cipherText);

        System.out.print("Decrypted: ");
        print(decryptedText);

        sc.close(); 
    }

    static int[][] genKeys(String key) {
        int[] b = perm(toBits(key), P10);
        int[] L = Arrays.copyOfRange(b, 0, 5);
        int[] R = Arrays.copyOfRange(b, 5, 10);

        L = LS(L, 1);
        R = LS(R, 1);
        int[] K1 = perm(concat(L, R), P8);

        L = LS(L, 2);
        R = LS(R, 2);
        int[] K2 = perm(concat(L, R), P8);

        return new int[][] {K1, K2};
    }

    static int[] encrypt(int[] b, int[][] k) {
        b = perm(b, IP);
        b = fk(b, k[0]);
        b = swap(b);
        b = fk(b, k[1]);
        return perm(b, IPi);
    }

    static int[] decrypt(int[] b, int[][] k) {
        b = perm(b, IP);
        b = fk(b, k[1]);
        b = swap(b);
        b = fk(b, k[0]);
        return perm(b, IPi);
    }

    static int[] fk(int[] bits, int[] key) {
        int[] L = Arrays.copyOfRange(bits, 0, 4);
        int[] R = Arrays.copyOfRange(bits, 4, 8);
        int[] ep = perm(R, EP);

        for (int i = 0; i < 8; i++) {
            ep[i] ^= key[i];
        }

        int[] leftS = S(Arrays.copyOfRange(ep, 0, 4), S0);
        int[] rightS = S(Arrays.copyOfRange(ep, 4, 8), S1);

        int[] p4 = perm(concat(leftS, rightS), P4);

        for (int i = 0; i < 4; i++) {
            L[i] ^= p4[i];
        }

        return concat(L, R);
    }

    static int[] swap(int[] b) {
        return concat(Arrays.copyOfRange(b, 4, 8), Arrays.copyOfRange(b, 0, 4));
    }

    static int[] S(int[] in, int[][] S) {
        int row = (in[0] << 1) | in[3];
        int col = (in[1] << 1) | in[2];
        int val = S[row][col];
        return new int[] { (val >> 1) & 1, val & 1 };
    }

    static int[] perm(int[] in, int[] p) {
        int[] out = new int[p.length];
        for (int i = 0; i < p.length; i++) {
            out[i] = in[p[i] - 1];
        }
        return out;
    }

    static int[] LS(int[] bits, int n) {
        int[] res = new int[bits.length];
        for (int i = 0; i < bits.length; i++) {
            res[i] = bits[(i + n) % bits.length];
        }
        return res;
    }

    static int[] concat(int[] a, int[] b) {
        int[] r = new int[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    static int[] toBits(String s) {
        return s.chars().map(c -> c - '0').toArray();
    }

    static void print(int[] b) {
        for (int x : b) System.out.print(x);
        System.out.println();
    }
}
