import java.security.SecureRandom;

public class Util {
    private static final SecureRandom random = new SecureRandom();

    // Source - https://stackoverflow.com/a/14257525
    // Posted by Eldelshell, modified by community.
    // Retrieved 2025-11-19, License - CC BY-SA 3.0.
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public static int randomInt(int min, int max) {
        return random.nextInt(min, max);
    }

    public static int randomInt(int max) {
        return random.nextInt(max);
    }

    public static boolean randomBool() {
        return random.nextBoolean();
    }
}
