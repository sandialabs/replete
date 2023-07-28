package finio;

import static finio.core.impl.FMap.A;

import org.junit.Test;

import finio.core.impl.FMap;

public class FMapTest {
    @Test
    public void overlayWith() {
        FMap A = A("A", 1, "B", 2, "C", A("D", 4, "E", 5));
        FMap B = A("A", 10, "C", A("D", 40, "X", 77), "Q", 55, "R", A("V", 2));
        System.out.println("A="+A);
        System.out.println("B="+B);
        System.out.println("B>>A=" + FMap.overlayWith(A, B));
        System.out.println("A>>B=" + FMap.overlayWith(B, A));
    }

    private void overlayHelper(FMap Mbot, FMap Mtop, FMap Mresult) {
        FMap Mbot1 = Mbot.copy();
        Mtop.overlayOnto(Mbot);
        Mtop.overlayOnto(Mbot, false);
        FMap Mbot2 = Mbot.copy();
        Mbot.overlayWith(Mtop);
        Mbot.overlayWith(Mtop, false);
        // TODO
    }
}
