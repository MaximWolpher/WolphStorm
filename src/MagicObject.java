public class MagicObject {

    public long magic;
    public int shift;
    public long mask;
    public long[] attacks;

    public MagicObject(long mask, long magic, int shift, long[] attacks) {
        this.mask = mask;
        this.magic = magic;
        this.shift = shift;
        this.attacks = attacks;
    }

    public MagicObject(){
        this.mask = 0L;
        this.magic = 0L;
        this.shift = 0;
        this.attacks = new long[0];
    }
}
