package crypto;

/**
 * Heavy-Duty encryption for Bob.
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1 $
 */
public class ROT_BOB {
    public static void main(String [] args) {
        if (args.length != 2) {
            System.err.println("Usage: ROT_X <message> <rotation (0-25)>");
            System.exit(-1);
        }
        final char [] chars = args[0].toUpperCase().toCharArray();
        final int rot = Integer.parseInt(args[1]);
        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            System.out.print(c < 65 || c > 90 ?
                             ' ' : (char) (((c - 65 + rot) % 26) + 65));
        }
        System.out.println();
    }
}
