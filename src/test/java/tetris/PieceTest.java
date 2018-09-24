package tetris;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/*
  Unit test for Piece class -- starter shell.
 */
public class PieceTest{
    Piece s1, s2, stick, l1, l2, square, pyramid;

    @Before
    public void setUp() throws Exception {
        s1 = new Piece("0 0	1 0 1 1 2 1");
        s2 = new Piece("0 1 1 1 1 0 2 0");
        l1 = new Piece("0 0	0 1	0 2 1 0");
        l2 = new Piece("0 0	1 0 1 1	1 2");
        stick = new Piece("0 0 0 1 0 2 0 3");
        square = new Piece("0 0 0 1 1 0 1 1");
        pyramid = new Piece("0 0 1 0 1 1 2 0");
    }

    @Test
    public void piecesOfDifferentOrderOfPointsShouldBeEqual() {
        assertEquals(stick, new Piece("0 3 0 0 0 1 0 2"));
        assertEquals(stick, new Piece("0 1 0 3 0 0 0 2"));
        assertEquals(stick, new Piece("0 2 0 3 0 0 0 1"));
        assertEquals(stick, new Piece("0 2 0 3 0 1 0 0"));
        assertEquals(stick, new Piece("0 1 0 2 0 3 0 0"));
        assertEquals(stick, new Piece("0 3 0 0 0 2 0 1"));
        assertEquals(stick, new Piece("0 0 0 1 0 3 0 2"));
    }

    @Test
    public void differentPiecesShouldNotBeEqual() {
        assertFalse(stick.equals(s1));
        Piece horizontalStick = new Piece("1 0 3 0 0 0 2 0");
        assertFalse(stick.equals(horizontalStick));
    }

    @Test
    public void testWidth() {
        assertEquals(1, stick.getWidth());
        assertEquals(3, s1.getWidth());
        assertEquals(2, l1.getWidth());
        assertEquals(4, (new Piece("1 0 3 0 0 0 2 0")).getWidth());
    }

    @Test
    public void testHeight() {
        assertEquals(4, stick.getHeight());
        assertEquals(2, s1.getHeight());
        assertEquals(3, l1.getHeight());
        assertEquals(1, (new Piece("1 0 3 0 0 0 2 0")).getHeight());
    }

    @Test
    public void testSkirt() {
        assertTrue(Arrays.equals(new int[]{0, 0, 0}, (new Piece("0 0 1 0 1 1 2 0")).getSkirt()));
        assertTrue(Arrays.equals(new int[] {0}, (new Piece("0 0 0 1 0 2 0 3")).getSkirt()));
        assertTrue(Arrays.equals(new int[] {0, 0, 0, 0}, (new Piece("1 0 3 0 0 0 2 0")).getSkirt()));
        assertTrue(Arrays.equals(new int[] {0, 0, 1}, (new Piece("0 0 1 0 1 1 2 1")).getSkirt()));
        assertTrue(Arrays.equals(new int[] {0, 1}, (new Piece("0 0 0 1 1 1 1 2")).getSkirt()));
        assertTrue(Arrays.equals(new int[] {1, 0, 1}, (new Piece("0 1 1 0 1 1 2 1")).getSkirt()));
        assertTrue(Arrays.equals(new int[] {1, 1, 0}, (new Piece("0 1 1 1 2 1  2 0")).getSkirt()));
        assertTrue(Arrays.equals(new int[] {0, 0}, (new Piece("0 0 1 0 1 1 0 1")).getSkirt()));
        assertTrue(Arrays.equals(new int[] {0, 2}, (new Piece("0 0 0 1 0 2 1 2")).getSkirt()));
        assertTrue(Arrays.equals(new int[] {2, 0}, (new Piece("1 0 1 1 1 2 0 2")).getSkirt()));
         }

    @Test
    public void testComputeNextRotation() {
        //test for STICK
        Piece horizontalStick = new Piece("0 0 1 0 2 0 3 0");
        assertEquals(horizontalStick, stick.computeNextRotation());
        assertEquals(stick, horizontalStick.computeNextRotation());

        //test for L1
        Piece l1Rotation = new Piece("2 0 1 0 0 0 2 1");
        Piece l1Rotation2 = new Piece("1 0 1 1 1 2 0 2");
        Piece l1Rotation3 = new Piece("0 0 0 1 1 1 2 1");
        assertEquals(l1Rotation, l1.computeNextRotation());
        assertEquals(l1Rotation2, l1Rotation.computeNextRotation());
        assertEquals(l1Rotation3, l1Rotation2.computeNextRotation());
        assertEquals(l1, l1Rotation3.computeNextRotation());

        //test for L2
        Piece l2Rotation1 = new Piece("0 1 1 1 2 1 2 0");
        Piece l2Rotation2 = new Piece("0 0 0 1 0 2 1 2");
        Piece l2Rotation3 = new Piece("0 0 1 0 2 0 0 1");
        assertEquals(l2Rotation1, l2.computeNextRotation());
        assertEquals(l2Rotation2, l2Rotation1.computeNextRotation());
        assertEquals(l2Rotation3, l2Rotation2.computeNextRotation());
        assertEquals(l2, l2Rotation3.computeNextRotation());

        //test for S1
        Piece s1Rotation = new Piece("1 0 1 1 0 1 0 2");
        assertEquals(s1Rotation, s1.computeNextRotation());
        assertEquals(s1, s1Rotation.computeNextRotation());

        //test for S2
        Piece s2Rotation = new Piece("0 0 0 1 1 1 1 2");
        assertEquals(s2Rotation, s2.computeNextRotation());
        assertEquals(s2, s2Rotation.computeNextRotation());

        //test for PYRAMID
        Piece pyramidRotation1 = new Piece("1 0 0 1 1 1 1 2");
        Piece pyramidRotation2 = new Piece("1 0 0 1 1 1 2 1");
        Piece pyramidRotation3 = new Piece("0 0 0 1 0 2 1 1");
        assertEquals(pyramidRotation1, pyramid.computeNextRotation());
        assertEquals(pyramidRotation2, pyramidRotation1.computeNextRotation());
        assertEquals(pyramidRotation3, pyramidRotation2.computeNextRotation());
        assertEquals(pyramid, pyramidRotation3.computeNextRotation());
    }

    @Test
    public void makeFastRotationCreatesCircularStructures() {
        Piece[] pieces = Piece.getPieces();
        Piece first = pieces[Piece.STICK];
        assertTrue(first == first.fastRotation().fastRotation());

        first = pieces[Piece.SQUARE];
        assertTrue(first == first.fastRotation());

        first = pieces[Piece.S1];
        assertTrue(first == first.fastRotation().fastRotation().fastRotation().fastRotation());

        first = pieces[Piece.S2];
        assertTrue(first == first.fastRotation().fastRotation().fastRotation().fastRotation());

        first = pieces[Piece.L1];
        assertTrue(first == first.fastRotation().fastRotation().fastRotation().fastRotation());

        first = pieces[Piece.L2];
        assertTrue(first == first.fastRotation().fastRotation().fastRotation().fastRotation());

        first = pieces[Piece.PYRAMID];
        assertTrue(first == first.fastRotation().fastRotation().fastRotation().fastRotation());
    }

    @Test
    public void testFastRotation() {
        Piece[] pieces = Piece.getPieces();

        //test for Stick
        Piece horizontalStick = new Piece("0 0 1 0 2 0 3 0");
        assertEquals(horizontalStick, pieces[Piece.STICK].fastRotation());

        //test for L1
        Piece l1Rotation = new Piece("2 0 1 0 0 0 2 1");
        Piece l1Rotation2 = new Piece("1 0 1 1 1 2 0 2");
        Piece l1Rotation3 = new Piece("0 0 0 1 1 1 2 1");
        assertEquals(l1Rotation, pieces[Piece.L1].fastRotation());
        assertEquals(l1Rotation2, pieces[Piece.L1].fastRotation().fastRotation());
        assertEquals(l1Rotation3, pieces[Piece.L1].fastRotation().fastRotation().fastRotation());


        //test for L2
        Piece l2Rotation1 = new Piece("0 1 1 1 2 1 2 0");
        Piece l2Rotation2 = new Piece("0 0 0 1 0 2 1 2");
        Piece l2Rotation3 = new Piece("0 0 1 0 2 0 0 1");
        assertEquals(l2Rotation1, pieces[Piece.L2].fastRotation());
        assertEquals(l2Rotation2, pieces[Piece.L2].fastRotation().fastRotation());
        assertEquals(l2Rotation3, pieces[Piece.L2].fastRotation().fastRotation().fastRotation());


        //test for S1
        Piece s1Rotation = new Piece("1 0 1 1 0 1 0 2");
        assertEquals(s1Rotation, pieces[Piece.S1].fastRotation());

        //test for S2
        Piece s2Rotation = new Piece("0 0 0 1 1 1 1 2");
        assertEquals(s2Rotation, pieces[Piece.S2].fastRotation());

        //test for PYRAMID
        Piece pyramidRotation1 = new Piece("1 0 0 1 1 1 1 2");
        Piece pyramidRotation2 = new Piece("1 0 0 1 1 1 2 1");
        Piece pyramidRotation3 = new Piece("0 0 0 1 0 2 1 1");
        assertEquals(pyramidRotation1, pieces[Piece.PYRAMID].fastRotation());
        assertEquals(pyramidRotation2, pieces[Piece.PYRAMID].fastRotation().fastRotation());
        assertEquals(pyramidRotation3, pieces[Piece.PYRAMID].fastRotation().fastRotation().fastRotation());
    }
}